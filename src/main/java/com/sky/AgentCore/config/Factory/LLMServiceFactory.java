package com.sky.AgentCore.config.Factory;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.service.llm.provider.Provider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** LLM服务工厂，用于创建LLM客户端 */
@Component
public class LLMServiceFactory {
    private final ProviderRegistry providerRegistry;

    @Autowired
    public LLMServiceFactory(ProviderRegistry providerRegistry) {
        this.providerRegistry = providerRegistry;
    }

    /**
     * 获取流式LLM客户端
     * 
     * @param provider 服务商实体
     * @param model 模型实体
     * @return 流式聊天语言模型
     */
    public StreamingChatModel getStreamingClient(ProviderEntity provider, ModelEntity model,
            LLMModelConfig llmModelConfig) {
        ProviderConfig providerConfig = new ProviderConfig(
                provider.getConfig().getApiKey(), provider.getConfig().getBaseUrl(),
                model.getModelEndpoint(), provider.getProtocol());

        Provider p = providerRegistry.get(provider.getProtocol());
        return p.createStreamingChatModel(providerConfig, llmModelConfig);
    }

    /**
     * 获取标准LLM客户端
     *
     * @param provider 服务商实体
     * @param model    模型实体
     * @return 流式聊天语言模型
     */
    public ChatModel getStrandClient(ProviderEntity provider, ModelEntity model) {
        ProviderConfig config = new ProviderConfig();
        config.setApiKey(provider.getConfig().getApiKey());
        config.setBaseUrl(provider.getConfig().getBaseUrl());

        ProviderConfig providerConfig = new ProviderConfig(config.getApiKey(), config.getBaseUrl(),
                model.getModelEndpoint(), provider.getProtocol());

        Provider p = providerRegistry.get(provider.getProtocol());
        return p.createChatModel(providerConfig);
    }
}
