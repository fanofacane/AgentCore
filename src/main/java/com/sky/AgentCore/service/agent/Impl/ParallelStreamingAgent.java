package com.sky.AgentCore.service.agent.Impl;

import com.sky.AgentCore.service.agent.Agent;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import dev.langchain4j.service.tool.ToolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 支持并行工具调用的流式Agent实现
 */
public class ParallelStreamingAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(ParallelStreamingAgent.class);

    private final StreamingChatModel chatModel;
    private final MessageWindowChatMemory chatMemory;
    private final Map<String, ToolExecutor> toolExecutorMap;
    private final List<ToolSpecification> toolSpecifications;
    
    // 使用缓存线程池来并发执行工具
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ParallelStreamingAgent(StreamingChatModel chatModel, MessageWindowChatMemory chatMemory, Map<ToolSpecification, ToolExecutor> tools) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
        this.toolSpecifications = new ArrayList<>(tools.keySet());
        this.toolExecutorMap = new HashMap<>();
        
        for (Map.Entry<ToolSpecification, ToolExecutor> entry : tools.entrySet()) {
            this.toolExecutorMap.put(entry.getKey().name(), entry.getValue());
        }
    }

    @Override
    public TokenStream chat(String userMessage) {
        return new TokenStream() {
            private Consumer<String> onPartialResponseHandler;
            private Consumer<ChatResponse> onCompleteResponseHandler;
            private Consumer<Throwable> onErrorHandler;
            private Consumer<ToolExecution> onToolExecutedHandler;

            @Override
            public TokenStream onPartialResponse(Consumer<String> handler) {
                this.onPartialResponseHandler = handler;
                return this;
            }

            @Override
            public TokenStream onRetrieved(Consumer<List<Content>> consumer) {
                return null;
            }

            @Override
            public TokenStream onCompleteResponse(Consumer<ChatResponse> handler) {
                this.onCompleteResponseHandler = handler;
                return this;
            }

            @Override
            public TokenStream onError(Consumer<Throwable> handler) {
                this.onErrorHandler = handler;
                return this;
            }

            @Override
            public TokenStream onToolExecuted(Consumer<ToolExecution> handler) {
                this.onToolExecutedHandler = handler;
                return this;
            }

            @Override
            public TokenStream ignoreErrors() {
                return this;
            }

            @Override
            public void start() {
                try {
                    chatMemory.add(new UserMessage(userMessage));
                    doChat();
                } catch (Exception e) {
                    if (onErrorHandler != null) {
                        onErrorHandler.accept(e);
                    } else {
                        logger.error("Error starting chat", e);
                    }
                }
            }

            private void doChat() {
                List<ChatMessage> messages = chatMemory.messages();
                
                ChatRequest chatRequest = ChatRequest.builder()
                        .messages(messages)
                        .toolSpecifications(toolSpecifications)
                        .build();

                chatModel.chat(chatRequest, new StreamingChatResponseHandler() {
                    @Override
                    public void onPartialResponse(String token) {
                        if (onPartialResponseHandler != null) {
                            onPartialResponseHandler.accept(token);
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse completeResponse) {
                        AiMessage aiMessage = completeResponse.aiMessage();
                        
                        if (aiMessage.hasToolExecutionRequests()) {
                            chatMemory.add(aiMessage);
                            
                            // 并行执行工具
                            List<CompletableFuture<Void>> futures = aiMessage.toolExecutionRequests().stream()
                                    .map(request -> CompletableFuture.runAsync(() -> {
                                        try {
                                            ToolExecutor toolExecutor = toolExecutorMap.get(request.name());
                                            if (toolExecutor == null) {
                                                throw new RuntimeException("Tool not found: " + request.name());
                                            }
                                            
                                            logger.debug("Executing tool: {}", request.name());
                                            // memoryId 传 null，根据 AbstractBuiltInToolProvider 实现
                                            String result = toolExecutor.execute(request, null);
                                            
                                            // 通知工具执行回调
                                            if (onToolExecutedHandler != null) {
                                                // 构造 ToolExecution 对象
                                                onToolExecutedHandler.accept(ToolExecution.builder()
                                                        .request(request)
                                                        .result(result)
                                                        .build());
                                            }
                                            
                                            synchronized (chatMemory) {
                                                chatMemory.add(new ToolExecutionResultMessage(request.id(), request.name(), result));
                                            }
                                        } catch (Exception e) {
                                            logger.error("Error executing tool: " + request.name(), e);
                                            synchronized (chatMemory) {
                                                 chatMemory.add(new ToolExecutionResultMessage(request.id(), request.name(), "Error: " + e.getMessage()));
                                            }
                                        }
                                    }, executorService))
                                    .collect(Collectors.toList());

                            // 等待所有工具执行完成
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                            // 递归调用下一轮
                            doChat();
                        } else {
                            chatMemory.add(aiMessage);
                            if (onCompleteResponseHandler != null) {
                                onCompleteResponseHandler.accept(completeResponse);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        if (onErrorHandler != null) {
                            onErrorHandler.accept(error);
                        } else {
                            logger.error("Error during chat streaming", error);
                        }
                    }
                });
            }
        };
    }
}
