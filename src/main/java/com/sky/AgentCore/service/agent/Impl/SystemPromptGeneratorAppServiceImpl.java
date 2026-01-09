package com.sky.AgentCore.service.agent.Impl;

import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.config.Factory.LLMServiceFactory;
import com.sky.AgentCore.dto.agent.SystemPromptGenerateRequest;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.tool.ToolEntity;
import com.sky.AgentCore.service.agent.SystemPromptGeneratorAppService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.tool.ToolService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemPromptGeneratorAppServiceImpl implements SystemPromptGeneratorAppService {
    @Autowired
    private UserSettingsDomainService userSettingsDomainService;
    @Autowired
    private LLMDomainService llmDomainService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private LLMServiceFactory llmServiceFactory;
    @Autowired SystemPrompt systemPrompt;
    @Override
    public String generateSystemPrompt(SystemPromptGenerateRequest request, String userId) {
        // 1. 应用层协调各个领域服务获取数据
        String defaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        if (defaultModelId == null) throw new BusinessException("未设置默认模型");

        // 2. 获取模型和提供商信息
        ModelEntity model = llmDomainService.selectModelById(defaultModelId);
        ProviderEntity provider = llmDomainService.getProvider(model.getProviderId());

        // 3. 获取工具详细信息
        List<ToolEntity> tools = new ArrayList<>();
        if (request.getToolIds() != null && !request.getToolIds().isEmpty()) {
            tools = toolService.getByIds(request.getToolIds());
        }

        // 4. 创建LLM客户端
        ChatModel chatModel = llmServiceFactory.getStrandClient(provider, model);

        // 5. 调用系统提示词生成领域服务（只负责核心生成逻辑）
        return systemPrompt.generateSystemPrompt(request.getAgentName(),
                request.getAgentDescription(),request.getExistingPrompt(), tools, chatModel);
    }
}
