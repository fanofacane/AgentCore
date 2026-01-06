package com.sky.AgentCore.service.llm.provider;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AnthropicProvider implements Provider {
    @Override
    public ProviderProtocol getProtocol() {
        return ProviderProtocol.ANTHROPIC;
    }

    @Override
    public ChatModel createChatModel(ProviderConfig providerConfig) {
        return AnthropicChatModel.builder()
                .apiKey(providerConfig.getApiKey())
                .baseUrl(providerConfig.getBaseUrl())
                .modelName(providerConfig.getModel())
                .version("2023-06-01")
                .timeout(Duration.ofHours(1))
                .build();
    }

    @Override
    public StreamingChatModel createStreamingChatModel(ProviderConfig providerConfig, LLMModelConfig llmModelConfig) {
        if (llmModelConfig == null) llmModelConfig = new LLMModelConfig();

        return AnthropicStreamingChatModel.builder()
                .apiKey(providerConfig.getApiKey())
                .baseUrl(providerConfig.getBaseUrl())
                .version("2023-06-01")
                .modelName(providerConfig.getModel())
                .temperature(llmModelConfig.getTemperature())
                .timeout(Duration.ofMinutes(5))
                .build();
    }
}

