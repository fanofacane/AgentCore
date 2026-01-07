package com.sky.AgentCore.service.llm.provider;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class OpenAIProvider implements Provider {
    @Override
    public ProviderProtocol getProtocol() {
        return ProviderProtocol.OPENAI;
    }

    @Override
    public ChatModel createChatModel(ProviderConfig providerConfig) {
        return OpenAiChatModel.builder()
                .apiKey(providerConfig.getApiKey())
                .baseUrl(providerConfig.getBaseUrl())
                .customHeaders(providerConfig.getCustomHeaders())
                .modelName(providerConfig.getModel())
                .timeout(Duration.ofHours(1))
                .build();
    }

    @Override
    public StreamingChatModel createStreamingChatModel(ProviderConfig providerConfig, LLMModelConfig llmModelConfig) {
        if (llmModelConfig == null) llmModelConfig = new LLMModelConfig();

        return new OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder()
                .apiKey(providerConfig.getApiKey())
                .baseUrl(providerConfig.getBaseUrl())
                .customHeaders(providerConfig.getCustomHeaders())
                .temperature(llmModelConfig.getTemperature())
                .modelName(providerConfig.getModel())
                .timeout(Duration.ofMinutes(5))
                .build();
    }
}

