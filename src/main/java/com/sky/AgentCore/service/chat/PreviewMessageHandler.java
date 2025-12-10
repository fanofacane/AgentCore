package com.sky.AgentCore.service.chat;

import com.sky.AgentCore.config.LLMServiceFactory;
import com.sky.AgentCore.dto.agent.AgentChatResponse;
import com.sky.AgentCore.dto.chat.ChatContext;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.enums.MessageType;
import com.sky.AgentCore.service.agent.Agent;
import com.sky.AgentCore.service.service.AbstractMessageHandler;
import com.sky.AgentCore.transport.MessageTransport;
import dev.langchain4j.service.TokenStream;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/** 预览消息处理器 专门用于Agent预览功能，不会保存消息到数据库 */
@Component(value = "previewMessageHandler")
public class PreviewMessageHandler extends AbstractMessageHandler {
    public PreviewMessageHandler(LLMServiceFactory llmServiceFactory, MessageService messageDomainService) {
        super(llmServiceFactory, messageDomainService);
    }

    /** 预览专用的聊天处理逻辑 与正常流程的区别是不保存消息到数据库 */
    @Override
    protected <T> void processChat(Agent agent, T connection, MessageTransport<T> transport, ChatContext chatContext,
                                   MessageEntity userEntity, MessageEntity llmEntity) {

        AtomicReference<StringBuilder> messageBuilder = new AtomicReference<>(new StringBuilder());

        TokenStream tokenStream = agent.chat(chatContext.getUserMessage());

        tokenStream.onError(throwable -> {
            transport.sendMessage(connection,
                    AgentChatResponse.buildEndMessage(throwable.getMessage(), MessageType.TEXT));
        });

        // 部分响应处理
        tokenStream.onPartialResponse(reply -> {
            messageBuilder.get().append(reply);
            // 删除换行后消息为空字符串
            if (messageBuilder.get().toString().trim().isEmpty()) {
                return;
            }
            transport.sendMessage(connection, AgentChatResponse.build(reply, MessageType.TEXT));
        });

        // 完整响应处理
        tokenStream.onCompleteResponse(chatResponse -> {
            // 发送结束消息
            transport.sendEndMessage(connection, AgentChatResponse.buildEndMessage(MessageType.TEXT));

            // 执行模型调用计费
/*            performBillingWithErrorHandling(chatContext, chatResponse.tokenUsage().inputTokenCount(),
                    chatResponse.tokenUsage().outputTokenCount(), transport, connection);*/
        });

        // 工具执行处理
        tokenStream.onToolExecuted(toolExecution -> {
            if (messageBuilder.get().length() > 0) {
                transport.sendMessage(connection, AgentChatResponse.buildEndMessage(MessageType.TEXT));
                llmEntity.setContent(messageBuilder.toString());

                messageBuilder.set(new StringBuilder());
            }
            String message = "执行工具：" + toolExecution.request().name();
            MessageEntity toolMessage = createLlmMessage(chatContext);
            toolMessage.setMessageType(MessageType.TOOL_CALL);
            toolMessage.setContent(message);
            transport.sendMessage(connection, AgentChatResponse.buildEndMessage(message, MessageType.TOOL_CALL));
        });

        // 启动流处理
        tokenStream.start();
    }

}
