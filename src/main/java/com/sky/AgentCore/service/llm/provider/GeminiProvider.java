package com.sky.AgentCore.service.llm.provider;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class GeminiProvider implements Provider {
    @Override
    public ProviderProtocol getProtocol() {
        return ProviderProtocol.Google;
    }

    @Override
    public ChatModel createChatModel(ProviderConfig providerConfig) {
        throw new UnsupportedOperationException("Gemini chat model not supported");
    }

    @Override
    public StreamingChatModel createStreamingChatModel(ProviderConfig providerConfig, LLMModelConfig llmModelConfig) {
        if (llmModelConfig == null) llmModelConfig = new LLMModelConfig();

        return GoogleAiGeminiStreamingChatModel.builder()
                .apiKey(providerConfig.getApiKey())
                .baseUrl(providerConfig.getBaseUrl())
                .modelName(providerConfig.getModel())
                .temperature(llmModelConfig.getTemperature())
                .timeout(Duration.ofMinutes(5))
                .build();
    }
}

