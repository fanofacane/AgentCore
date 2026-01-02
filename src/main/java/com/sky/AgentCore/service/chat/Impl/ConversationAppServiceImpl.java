package com.sky.AgentCore.service.chat.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.config.Factory.MessageHandlerFactory;
import com.sky.AgentCore.converter.assembler.MessageAssembler;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.dto.chat.*;
import com.sky.AgentCore.dto.gateway.HighAvailabilityResult;
import com.sky.AgentCore.dto.message.MessageDTO;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.message.TokenMessage;
import com.sky.AgentCore.dto.message.TokenProcessResult;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.dto.tool.UserToolEntity;
import com.sky.AgentCore.enums.MessageType;
import com.sky.AgentCore.enums.Role;
import com.sky.AgentCore.enums.TokenOverflowStrategyEnum;
import com.sky.AgentCore.service.agent.AgentAppService;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.agent.SessionService;
import com.sky.AgentCore.service.chat.ContextService;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.service.chat.MessageService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.chat.handler.PreviewMessageHandler;
import com.sky.AgentCore.service.chat.handler.AbstractMessageHandler;
import com.sky.AgentCore.service.gateway.HighAvailabilityService;
import com.sky.AgentCore.service.service.strategy.TokenOverflowConfig;
import com.sky.AgentCore.service.service.strategy.TokenService;
import com.sky.AgentCore.service.tool.UserToolService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import com.sky.AgentCore.service.chat.MessageTransport;
import com.sky.AgentCore.config.Factory.MessageTransportFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConversationAppServiceImpl implements ConversationAppService {
    @Autowired
    private  MessageTransportFactory transportFactory;
    @Autowired
    private UserSettingsDomainService userSettingsDomainService;
    @Autowired
    private LLMDomainService llmDomainService;
    @Autowired
    private PreviewMessageHandler previewMessageHandler;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ContextService contextService;
    @Autowired
    private AgentAppService agentAppService;
    @Autowired
    private AgentWorkspaceService agentWorkspaceService;
    @Autowired
    private UserToolService userToolService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RagSessionManager ragSessionManager;
    @Autowired
    private MessageHandlerFactory messageHandlerFactory;
    @Autowired
    private HighAvailabilityService highAvailabilityService;
    @Autowired
    private ChatSessionManager chatSessionManager;
    @Override
    public SseEmitter previewAgent(AgentPreviewRequest previewRequest, String userId) {
        // 1. 准备预览环境
        ChatContext environment = preparePreviewEnvironment(previewRequest, userId);

        // 2. 获取传输方式
        MessageTransport<SseEmitter> transport = transportFactory
                .getTransport(MessageTransportFactory.TRANSPORT_TYPE_SSE);
        // 3. 使用预览专用的消息处理器
        return previewMessageHandler.chat(environment, transport);
    }

    @Override
    public List<MessageDTO> getConversationMessages(String sessionId, String userId) {
        // 查询对应会话是否存在
        sessionService.find(sessionId, userId);

        List<MessageEntity> conversationMessages = messageService.getConversationMessages(sessionId);
        return MessageAssembler.toDTOs(conversationMessages);
    }
    /** 对话方法 - 统一入口，支持根据请求类型自动选择处理器
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return SSE发射器 */
    @Override
    public SseEmitter chat(ChatRequest chatRequest, String userId) {

        // 1. 根据请求类型准备对话环境
        ChatContext environment = prepareEnvironmentByRequestType(chatRequest, userId);

        // 2. 获取传输方式 (当前仅支持SSE，将来支持WebSocket)
        MessageTransport<SseEmitter> transport = transportFactory
                .getTransport(MessageTransportFactory.TRANSPORT_TYPE_SSE);

        // 3. 根据请求类型获取适合的消息处理器
        AbstractMessageHandler handler = messageHandlerFactory.getHandler(chatRequest);

        // 4. 处理对话
        SseEmitter emitter = handler.chat(environment, transport);

        // 5. 注册会话到会话管理器（支持中断功能）
        chatSessionManager.registerSession(chatRequest.getSessionId(), emitter);

        return emitter;
    }

    /** 对话处理（支持指定模型）- 用于外部API
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @param modelId 指定的模型ID（可选，为null时使用Agent绑定的模型）
     * @return SSE发射器 */
    @Override
    public SseEmitter chatWithModel(ChatRequest chatRequest, String userId, String modelId) {
        // 1. 准备对话环境（支持指定模型）
        ChatContext environment = prepareEnvironmentWithModel(chatRequest, userId, modelId);

        // 2. 获取传输方式 (当前仅支持SSE，将来支持WebSocket)
        MessageTransport<SseEmitter> transport = transportFactory
                .getTransport(MessageTransportFactory.TRANSPORT_TYPE_SSE);

        // 3. 获取适合的消息处理器 (根据agent类型)
        AbstractMessageHandler handler = messageHandlerFactory.getHandler(environment.getAgent());
        // 4. 处理对话
        SseEmitter emitter = handler.chat(environment, transport);

        // 5. 注册会话到会话管理器（支持中断功能）
        chatSessionManager.registerSession(chatRequest.getSessionId(), emitter);

        return emitter;
    }

    /** 同步对话处理（支持指定模型）- 用于外部API
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @param modelId 指定的模型ID（可选，为null时使用Agent绑定的模型）
     * @return 同步聊天响应 */
    @Override
    public ChatResponse chatSyncWithModel(ChatRequest chatRequest, String userId, String modelId) {
        // 1. 准备对话环境（设置为非流式）
        ChatContext environment = prepareEnvironmentWithModel(chatRequest, userId, modelId);
        environment.setStreaming(false); // 设置为同步模式

        // 2. 获取同步传输方式
        MessageTransport<ChatResponse> transport = transportFactory
                .getTransport(MessageTransportFactory.TRANSPORT_TYPE_SYNC);

        // 3.获取适合的消息处理器
        AbstractMessageHandler handler = messageHandlerFactory.getHandler(environment.getAgent());

        // 4. 处理对话
        return handler.chat(environment, transport);
    }

    /** 根据请求类型准备环境
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return 聊天上下文 */
    private ChatContext prepareEnvironmentByRequestType(ChatRequest chatRequest, String userId) {
        if (chatRequest instanceof RagChatRequest) {
            return prepareRagEnvironment((RagChatRequest) chatRequest, userId);
        }

        // 标准对话环境准备
        return prepareEnvironment(chatRequest, userId);
    }
    /** 准备对话环境
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @return 对话环境 */
    private ChatContext prepareEnvironment(ChatRequest chatRequest, String userId) {
        return prepareEnvironmentWithModel(chatRequest, userId, null);
    }

    /** 准备对话环境（支持指定模型）- 用于外部API
     *
     * @param chatRequest 聊天请求
     * @param userId 用户ID
     * @param modelId 指定的模型ID（可选，为null时使用Agent绑定的模型）
     * @return 对话环境 */
    private ChatContext prepareEnvironmentWithModel(ChatRequest chatRequest, String userId, String modelId) {
        // 1. 获取会话和Agent信息
        String sessionId = chatRequest.getSessionId();
        SessionEntity session = sessionService.getSession(sessionId, userId);
        String agentId = session.getAgentId();
        AgentEntity agent = getAgentWithValidation(agentId, userId);

        // 2. 获取工具配置
        List<String> mcpServerNames = getMcpServerNames(agent.getToolIds(), userId);

        // 3. 获取模型配置
        AgentWorkspaceEntity workspace = agentWorkspaceService.getWorkspace(agentId, userId);
        LLMModelConfig llmModelConfig = workspace.getLlmModelConfig();

        ModelEntity originalModel = getModelForChat(llmModelConfig, modelId, userId);

        // 4. 获取高可用服务商信息
        List<String> fallbackChain = userSettingsDomainService.getUserFallbackChain(userId);
        HighAvailabilityResult result = highAvailabilityService.selectBestProvider(originalModel, userId, sessionId,
                fallbackChain);

        ProviderEntity originalProvider = llmDomainService.getProvider(originalModel.getProviderId());
        ProviderEntity provider = result.getProvider();
        ModelEntity model = result.getModel();
        String instanceId = result.getInstanceId();
        provider.isActive();
        // 5. 创建并配置环境对象
        ChatContext chatContext = createChatContext(chatRequest, userId, agent, originalModel, model, originalProvider,
                provider, llmModelConfig, mcpServerNames, instanceId);
        setupContextAndHistory(chatContext, chatRequest);

        return chatContext;
    }


    /** 设置上下文和历史消息
     *
     * @param environment 对话环境 */
    private void setupContextAndHistory(ChatContext environment, ChatRequest chatRequest) {
        String sessionId = environment.getSessionId();

        // 获取上下文
        ContextEntity contextEntity = contextService.findBySessionId(sessionId);
        List<MessageEntity> messageEntities = new ArrayList<>();

        if (contextEntity != null) {
            // 获取活跃消息(包括摘要)
            List<String> activeMessageIds = contextEntity.getActiveMessages();
            System.out.println("上下文"+contextEntity.getActiveMessages());
            if (activeMessageIds !=null && !activeMessageIds.isEmpty()) {
                System.out.println("进入");
                messageEntities = messageService.listByIds(activeMessageIds);
                // 应用Token溢出策略, 上下文历史消息以token策略返回的为准
                messageEntities = applyTokenOverflowStrategy(environment,
                        contextEntity, messageEntities);
            }
        } else {
            contextEntity = new ContextEntity();
            contextEntity.setSessionId(sessionId);
        }

        // 特殊处理当前对话的文件，因为在后续的对话中无法发送文件
        List<String> fileUrls = chatRequest.getFileUrls();
        if (!fileUrls.isEmpty()) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setRole(Role.USER);
            messageEntity.setFileUrls(fileUrls);
            messageEntities.add(messageEntity);
        }

        environment.setContextEntity(contextEntity);
        environment.setMessageHistory(messageEntities);
    }

    /** 应用Token溢出策略，返回处理后的历史消息
     *
     * @param environment 对话环境
     * @param contextEntity 上下文实体
     * @param messageEntities 消息实体列表 */
    private List<MessageEntity> applyTokenOverflowStrategy(ChatContext environment, ContextEntity contextEntity,
                                                           List<MessageEntity> messageEntities) {

        LLMModelConfig llmModelConfig = environment.getLlmModelConfig();
        ProviderEntity provider = environment.getProvider();

        // 处理Token溢出
        TokenOverflowStrategyEnum strategyType = llmModelConfig.getStrategyType();

        // Token处理
        List<TokenMessage> tokenMessages = tokenizeMessage(messageEntities);

        // 构造Token配置
        TokenOverflowConfig tokenOverflowConfig = new TokenOverflowConfig();
        tokenOverflowConfig.setStrategyType(strategyType);
        tokenOverflowConfig.setMaxTokens(llmModelConfig.getMaxTokens());
        tokenOverflowConfig.setSummaryThreshold(llmModelConfig.getSummaryThreshold());
        tokenOverflowConfig.setReserveRatio(llmModelConfig.getReserveRatio());

        // 设置提供商配置
        ProviderConfig providerConfig = provider.getConfig();
        tokenOverflowConfig.setProviderConfig(new ProviderConfig(providerConfig.getApiKey(),
                providerConfig.getBaseUrl(), environment.getModel().getModelId(), provider.getProtocol()));

        // 处理Token
        TokenProcessResult result = tokenService.processMessages(tokenMessages, tokenOverflowConfig);
        List<TokenMessage> retainedMessages = new ArrayList<>(tokenMessages);
        TokenMessage newSummaryMessage = null;
        // 更新上下文
        if (result.isProcessed()) {
            retainedMessages = result.getRetainedMessages();
            // 统一对 活跃消息进行时间升序排序
            List<String> retainedMessageIds = retainedMessages.stream()
                    .sorted(Comparator.comparing(TokenMessage::getCreatedAt)).map(TokenMessage::getId)
                    .collect(Collectors.toList());
            if (strategyType == TokenOverflowStrategyEnum.SUMMARIZE
                    && retainedMessages.getFirst().getRole().equals(Role.SUMMARY.name())) {
                newSummaryMessage = retainedMessages.getFirst();
                contextEntity.setSummary(newSummaryMessage.getContent());
            }

            contextEntity.setActiveMessages(retainedMessageIds);
        }
        Set<String> retainedMessageIdSet = retainedMessages.stream().map(TokenMessage::getId)
                .collect(Collectors.toSet());
        // 从messageEntity中过滤出保留的消息，防止Entity字段丢失
        List<MessageEntity> newHistoryMessages = messageEntities.stream()
                .filter(message -> retainedMessageIdSet.contains(message.getId()) && !message.isSummaryMessage())
                .collect(Collectors.toList());
        if (newSummaryMessage != null) {
            newHistoryMessages.addFirst(summaryMessageToEntity(newSummaryMessage, environment.getSessionId()));
        }
        return newHistoryMessages;
    }

    private MessageEntity summaryMessageToEntity(TokenMessage tokenMessage, String sessionId) {
        MessageEntity messageEntity = new MessageEntity();
        BeanUtil.copyProperties(tokenMessage, messageEntity);
        messageEntity.setRole(Role.fromCode(tokenMessage.getRole()));
        messageEntity.setSessionId(sessionId);
        messageEntity.setMessageType(MessageType.TEXT);
        return messageEntity;
    }


    /** 消息实体转换为token消息 */
    private List<TokenMessage> tokenizeMessage(List<MessageEntity> messageEntities) {
        return messageEntities.stream().map(message -> {
            TokenMessage tokenMessage = new TokenMessage();
            tokenMessage.setId(message.getId());
            tokenMessage.setRole(message.getRole().name());
            tokenMessage.setContent(message.getContent());
            tokenMessage.setTokenCount(message.getTokenCount());
            tokenMessage.setBodyTokenCount(message.getBodyTokenCount());
            tokenMessage.setCreatedAt(message.getCreatedAt());
            return tokenMessage;
        }).collect(Collectors.toList());
    }


    /** 获取MCP服务器名称列表 */
    private List<String> getMcpServerNames(List<String> toolIds, String userId) {
        if (toolIds == null || toolIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserToolEntity> installTool = userToolService.getInstallTool(toolIds, userId);
        return installTool.stream().map(UserToolEntity::getMcpServerName).collect(Collectors.toList());
    }
    /** 获取Agent并进行验证 */
    private AgentEntity getAgentWithValidation(String agentId, String userId) {
        AgentEntity agent = agentAppService.getAgentById(agentId);
        if (!agent.getUserId().equals(userId) && !agent.getEnabled()) {
            throw new BusinessException("agent已被禁用");
        }

        // 处理安装的助理版本
        if (!agent.getUserId().equals(userId)) {
            AgentVersionEntity latestAgentVersion = agentAppService.getLatestAgentVersion(agentId);
            BeanUtils.copyProperties(latestAgentVersion, agent);
        }

        return agent;
    }
    /** 准备RAG环境
     * @param ragRequest RAG聊天请求
     * @param userId 用户ID
     * @return RAG聊天上下文 */
    private RagChatContext prepareRagEnvironment(RagChatRequest ragRequest, String userId) {
        // 1. 获取会话上下文和历史消息
        String sessionId = ragRequest.getSessionId();
        ContextEntity contextEntity = contextService.findBySessionId(sessionId);
        List<MessageEntity> messageHistory = new ArrayList<>();

        if (contextEntity != null && contextEntity.getActiveMessages() != null) {
            messageHistory = messageService.listByIds(contextEntity.getActiveMessages());
        } else {
            contextEntity = new ContextEntity();
            contextEntity.setSessionId(sessionId);
        }

        // 2. 创建RAG专用Agent
        AgentEntity ragAgent = createRagAgent();

        // 3. 获取用户默认模型配置
        String userDefaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        ModelEntity model = llmDomainService.selectModelById(userDefaultModelId);
        List<String> fallbackChain = userSettingsDomainService.getUserFallbackChain(userId);
        //ProviderEntity provider = llmDomainService.getProvider(model.getProviderId());
        // 4. 获取高可用服务商
        HighAvailabilityResult result = highAvailabilityService.selectBestProvider(model, userId, sessionId,
                fallbackChain);
        ProviderEntity provider = result.getProvider();
        ModelEntity selectedModel = result.getModel();

        // 5. 构建RAG上下文
        RagChatContext ragContext = new RagChatContext();
        ragContext.setSessionId(sessionId);
        ragContext.setUserId(userId);
        ragContext.setUserMessage(ragRequest.getMessage());
        ragContext.setRagSearchRequest(ragRequest.toRagSearchRequest());
        ragContext.setUserRagId(ragRequest.getUserRagId());
        ragContext.setFileId(ragRequest.getFileId());
        ragContext.setAgent(ragAgent);
        ragContext.setModel(selectedModel);
        ragContext.setProvider(provider);
        ragContext.setInstanceId(result.getInstanceId());
        ragContext.setContextEntity(contextEntity);
        ragContext.setMessageHistory(messageHistory);
        ragContext.setStreaming(true);
        ragContext.setFileUrls(ragRequest.getFileUrls());

        return ragContext;
    }

    /** 准备预览对话环境
     *
     * @param previewRequest 预览请求
     * @param userId 用户ID
     * @return 预览对话环境 */
    private ChatContext preparePreviewEnvironment(AgentPreviewRequest previewRequest, String userId) {
        // 1. 创建虚拟Agent和获取模型
        AgentEntity virtualAgent = createVirtualAgent(previewRequest, userId);
        String modelId = getPreviewModelId(previewRequest, userId);
        ModelEntity originModel = llmDomainService.selectModelById(modelId);
        // 3. 获取工具配置
        List<String> mcpServerNames = List.of();
        //List<String> mcpServerNames = getMcpServerNames(previewRequest.getToolIds(), userId);

        // 4. 获取高可用服务商信息
        List<String> fallbackChain = userSettingsDomainService.getUserFallbackChain(userId);
        HighAvailabilityResult result = highAvailabilityService.selectBestProvider(originModel, userId, null, fallbackChain);
        ProviderEntity originalProvider = llmDomainService.getProvider(originModel.getProviderId());

        ProviderEntity provider = result.getProvider();
        ModelEntity model = result.getModel();
        String instanceId = result.getInstanceId();
        provider.isActive();
        // 5. 创建并配置环境对象
        ChatContext chatContext = createPreviewChatContext(previewRequest, userId, virtualAgent, model,
                provider, previewRequest.getLlmModelConfig(), mcpServerNames,instanceId);
        setupPreviewContextAndHistory(chatContext, previewRequest);

        return chatContext;
    }
    private AgentEntity createVirtualAgent(AgentPreviewRequest agentPreviewRequest,String userId){
        AgentEntity virtualAgent = new AgentEntity();
        BeanUtil.copyProperties(agentPreviewRequest,virtualAgent);
        virtualAgent.setId("preview-agent");
        virtualAgent.setName("预览助理");
        virtualAgent.setUserId(userId);
        virtualAgent.setEnabled(true);
        virtualAgent.setCreatedAt(LocalDateTime.now());
        virtualAgent.setUpdatedAt(LocalDateTime.now());
        return virtualAgent;
    }
    /** 获取预览使用的模型ID */
    private String getPreviewModelId(AgentPreviewRequest previewRequest, String userId) {
        String modelId = previewRequest.getModelId();
        if (modelId == null || modelId.trim().isEmpty()) {
            modelId = userSettingsDomainService.getUserDefaultModelId(userId);
            if (modelId == null) throw new BusinessException("用户未设置默认模型，且预览请求中未指定模型");
        }
        return modelId;
    }
    /** 获取对话使用的模型 */
    private ModelEntity getModelForChat(LLMModelConfig llmModelConfig, String specifiedModelId, String userId) {
        String finalModelId;
        if (specifiedModelId != null && !specifiedModelId.trim().isEmpty()) {
            finalModelId = specifiedModelId;
        } else {
            finalModelId = llmModelConfig.getModelId();
        }

        if (finalModelId == null) finalModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        if (finalModelId == null) throw new BusinessException("模型不存在");
        ModelEntity model = llmDomainService.selectModelById(finalModelId);
        model.isActive();
        return model;
    }
    /** 创建默认的LLM模型配置 */
    private LLMModelConfig createDefaultLLMModelConfig(String modelId) {
        LLMModelConfig llmModelConfig = new LLMModelConfig();
        llmModelConfig.setModelId(modelId);
        llmModelConfig.setTemperature(0.7);
        llmModelConfig.setTopP(0.9);
        llmModelConfig.setMaxTokens(4000);
        llmModelConfig.setStrategyType(TokenOverflowStrategyEnum.NONE);
        llmModelConfig.setSummaryThreshold(2000);
        return llmModelConfig;
    }
    /** 创建预览ChatContext对象 */
    private ChatContext createPreviewChatContext(AgentPreviewRequest previewRequest, String userId, AgentEntity agent,
                                                 ModelEntity model, ProviderEntity provider, LLMModelConfig llmModelConfig,
                                                 List<String> mcpServerNames,String instanceId) {
        ChatContext chatContext = new ChatContext();
        chatContext.setSessionId("preview-session");
        chatContext.setUserId(userId);
        chatContext.setInstanceId(instanceId);
        chatContext.setUserMessage(previewRequest.getUserMessage());
        chatContext.setAgent(agent);
        chatContext.setModel(model);
        chatContext.setProvider(provider);
        chatContext.setLlmModelConfig(llmModelConfig);
        chatContext.setMcpServerNames(mcpServerNames);
        chatContext.setFileUrls(previewRequest.getFileUrls());
        return chatContext;
    }
    /** 设置预览上下文和历史消息 */
    private void setupPreviewContextAndHistory(ChatContext environment, AgentPreviewRequest previewRequest) {
        // 创建虚拟上下文实体
        ContextEntity contextEntity = new ContextEntity();
        contextEntity.setSessionId("preview-session");
        contextEntity.setActiveMessages(new ArrayList<>());

        // 转换前端传入的历史消息为实体
        List<MessageEntity> messageEntities = new ArrayList<>();
        List<MessageDTO> messageHistory = previewRequest.getMessageHistory();
        if (messageHistory != null && !messageHistory.isEmpty()) {
            for (MessageDTO messageDTO : messageHistory) {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setId(messageDTO.getId());
                messageEntity.setRole(messageDTO.getRole());
                messageEntity.setContent(messageDTO.getContent());
                messageEntity.setSessionId("preview-session");
                messageEntity.setCreatedAt(messageDTO.getCreatedAt());
                messageEntity.setFileUrls(messageDTO.getFileUrls());
                messageEntity.setTokenCount(messageDTO.getRole() == Role.USER ? 50 : 100); // 预估token数
                messageEntities.add(messageEntity);
            }
        }
        // 特殊处理当前对话的文件，因为在后续的对话中无法发送文件
        List<String> fileUrls = previewRequest.getFileUrls();
        if (!fileUrls.isEmpty()) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setRole(Role.USER);
            messageEntity.setSessionId("preview-session");
            messageEntity.setFileUrls(fileUrls);
            messageEntities.add(messageEntity);
        }

        environment.setContextEntity(contextEntity);
        environment.setMessageHistory(messageEntities);
    }


    /** 创建ChatContext对象 */
    private ChatContext createChatContext(ChatRequest chatRequest, String userId, AgentEntity agent,
                                          ModelEntity originalModel, ModelEntity model, ProviderEntity originalProvider,
                                          ProviderEntity provider, LLMModelConfig llmModelConfig, List<String> mcpServerNames, String instanceId) {
        ChatContext chatContext = new ChatContext();
        chatContext.setSessionId(chatRequest.getSessionId());
        chatContext.setUserId(userId);
        chatContext.setUserMessage(chatRequest.getMessage());
        chatContext.setAgent(agent);
        chatContext.setOriginalModel(originalModel);
        chatContext.setModel(model);
        chatContext.setOriginalProvider(originalProvider);
        chatContext.setProvider(provider);
        chatContext.setLlmModelConfig(llmModelConfig);
        chatContext.setMcpServerNames(mcpServerNames);
        chatContext.setFileUrls(chatRequest.getFileUrls());
        chatContext.setInstanceId(instanceId);
        return chatContext;
    }
    /** 创建RAG专用的虚拟Agent
     * @return RAG Agent */
    private AgentEntity createRagAgent() {
        AgentEntity ragAgent = new AgentEntity();
        ragAgent.setId("system-rag-agent");
        ragAgent.setUserId("system");
        ragAgent.setName("RAG助手");
        ragAgent.setSystemPrompt("""
                你是一位专业的文档问答助手，专门基于提供的文档内容回答用户问题。
                你的回答应该准确、有帮助，并且要诚实地告知用户当文档中没有相关信息时的情况。
                请遵循以下原则：
                1. 优先基于提供的文档内容回答
                2. 如果文档中没有相关信息，请明确告知用户
                3. 使用清晰的Markdown格式组织回答
                4. 在适当的地方引用文档页码或来源
                """);
        ragAgent.setEnabled(true);
        return ragAgent;
    }

    // ========== RAG 支持方法 ==========

    /** RAG流式问答 - 基于数据集
     * @param request RAG流式聊天请求
     * @param userId 用户ID
     * @return SSE流式响应 */

    @Override
    public SseEmitter ragStreamChat(RagStreamChatRequest request, String userId) {
        // 1. 创建临时RAG会话
        String sessionId = ragSessionManager.createOrGetRagSession(userId);

        // 2. 转换为RagChatRequest
        RagChatRequest ragChatRequest = RagChatRequest.fromRagStreamChatRequest(request, sessionId);

        // 3. 使用通用的chat入口
        return chat(ragChatRequest, userId);
    }

    /** RAG流式问答 - 基于已安装知识库
     * @param request RAG流式聊天请求
     * @param userRagId 用户RAG ID
     * @param userId 用户ID
     * @return SSE流式响应 */
    @Override
    public SseEmitter ragStreamChatByUserRag(RagStreamChatRequest request, String userRagId, String userId) {
        // 1. 创建用户RAG专用会话
        String sessionId = ragSessionManager.createOrGetUserRagSession(userId, userRagId);

        // 2. 转换为RagChatRequest（包含userRagId）
        RagChatRequest ragChatRequest = RagChatRequest.fromRagStreamChatRequestWithUserRag(request, userRagId,
                sessionId);

        // 3. 使用通用的chat入口
        return chat(ragChatRequest, userId);
    }
}
