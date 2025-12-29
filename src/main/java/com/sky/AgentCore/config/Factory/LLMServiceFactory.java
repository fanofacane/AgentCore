package com.sky.AgentCore.config.Factory;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.service.llm.Impl.LLMProviderService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.stereotype.Component;

/** LLM服务工厂，用于创建LLM客户端 */
@Component
public class LLMServiceFactory {

    /** 获取流式LLM客户端
     * @param provider 服务商实体
     * @param model 模型实体
     * @return 流式聊天语言模型 */
    public StreamingChatModel getStreamingClient(ProviderEntity provider, ModelEntity model, LLMModelConfig llmModelConfig) {
        ProviderConfig providerConfig = new ProviderConfig(
                provider.getConfig().getApiKey(), provider.getConfig().getBaseUrl(),
                model.getModelEndpoint(), provider.getProtocol());

        return LLMProviderService.getStream(provider.getProtocol(), providerConfig,llmModelConfig);
    }

    /** 获取标准LLM客户端
     *
     * @param provider 服务商实体
     * @param model 模型实体
     * @return 流式聊天语言模型 */
    public ChatModel getStrandClient(ProviderEntity provider, ModelEntity model) {
        ProviderConfig config = new ProviderConfig();
        config.setApiKey(provider.getConfig().getApiKey());
        config.setBaseUrl(provider.getConfig().getBaseUrl());

        ProviderConfig providerConfig = new ProviderConfig(config.getApiKey(), config.getBaseUrl(),
                model.getModelEndpoint(), provider.getProtocol());

        return LLMProviderService.getStrand(provider.getProtocol(), providerConfig);
    }
}
