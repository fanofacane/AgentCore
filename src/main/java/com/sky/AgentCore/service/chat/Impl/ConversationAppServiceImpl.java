package com.sky.AgentCore.service.chat.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.*;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import com.sky.AgentCore.dto.chat.ChatContext;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageDTO;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.enums.Role;
import com.sky.AgentCore.enums.TokenOverflowStrategyEnum;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.service.llm.LlmDomainService;
import com.sky.AgentCore.service.chat.PreviewMessageHandler;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import com.sky.AgentCore.transport.MessageTransport;
import com.sky.AgentCore.transport.MessageTransportFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationAppServiceImpl implements ConversationAppService {
    @Autowired
    private  MessageTransportFactory transportFactory;
    @Autowired
    private UserSettingsDomainService userSettingsDomainService;
    @Autowired
    private LlmDomainService llmDomainService;
    @Autowired
    private PreviewMessageHandler previewMessageHandler;
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
    /** 准备预览对话环境
     *
     * @param previewRequest 预览请求
     * @param userId 用户ID
     * @return 预览对话环境 */
    private ChatContext preparePreviewEnvironment(AgentPreviewRequest previewRequest, String userId) {
        // 1. 创建虚拟Agent和获取模型
        AgentEntity virtualAgent = createVirtualAgent(previewRequest, userId);
        String modelId = getPreviewModelId(previewRequest, userId);
        ModelEntity model = getModelForChat(null, modelId, userId);

        // 2. 获取服务商信息（预览不使用高可用）
        ProviderEntity provider = llmDomainService.getProvider(model.getProviderId());
        provider.isActive();
        provider.isAvailable(provider.getUserId());
        // 3. 获取工具配置
        List<String> mcpServerNames = List.of();
        //List<String> mcpServerNames = getMcpServerNames(previewRequest.getToolIds(), userId);

        // 4. 创建预览配置
        LLMModelConfig llmModelConfig = createDefaultLLMModelConfig(modelId);

        // 5. 创建并配置环境对象
        ChatContext chatContext = createPreviewChatContext(previewRequest, userId, virtualAgent, model, provider,
                llmModelConfig, mcpServerNames);
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
            if (modelId == null) {
                throw new BusinessException("用户未设置默认模型，且预览请求中未指定模型");
            }
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

        ModelEntity model = new ModelEntity();
        if (finalModelId == null) {
            String userDefaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
            model = llmDomainService.selectModelById(userDefaultModelId);
        } else{
            model = llmDomainService.selectModelById(finalModelId);
        }
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
                                                 ModelEntity model, ProviderEntity provider, LLMModelConfig llmModelConfig, List<String> mcpServerNames) {
        ChatContext chatContext = new ChatContext();
        chatContext.setSessionId("preview-session");
        chatContext.setUserId(userId);
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
}
