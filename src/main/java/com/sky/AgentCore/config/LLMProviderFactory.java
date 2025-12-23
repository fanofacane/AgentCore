package com.sky.AgentCore.config;


import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.time.Duration;

public class LLMProviderFactory {

    /** 获取对应的服务商 不使用工厂模式，因为 OpenAiChatModel 没有无参构造器，并且其他类型的模型不能适配
     * @param protocol 协议
     * @param providerConfig 服务商信息 */
    public static ChatModel getLLMProvider(ProviderProtocol protocol, ProviderConfig providerConfig) {
        ChatModel model = null;
        if (protocol == ProviderProtocol.OPENAI) {
            model = OpenAiChatModel.builder().apiKey(providerConfig.getApiKey())
                    .baseUrl(providerConfig.getBaseUrl()).customHeaders(providerConfig.getCustomHeaders())
                    .modelName(providerConfig.getModel()).timeout(Duration.ofHours(1)).build();
        }
        else if (protocol == ProviderProtocol.ANTHROPIC) {
            model = AnthropicChatModel.builder().apiKey(providerConfig.getApiKey())
                    .baseUrl(providerConfig.getBaseUrl()).modelName(providerConfig.getModel())
                    .version("2023-06-01").timeout(Duration.ofHours(1)).build();
        }
        return model;
    }

    public static StreamingChatModel getLLMProviderByStream(ProviderProtocol protocol,
                                                            ProviderConfig providerConfig,
                                                            LLMModelConfig llmModelConfig) {
        StreamingChatModel model = null;
        if (protocol == ProviderProtocol.OPENAI) {
            model = new OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder().apiKey(providerConfig.getApiKey())
                    .baseUrl(providerConfig.getBaseUrl()).customHeaders(providerConfig.getCustomHeaders())
                    .temperature(llmModelConfig.getTemperature()).topP(llmModelConfig.getTopP())
                    .maxTokens(llmModelConfig.getMaxTokens())
                    .modelName(providerConfig.getModel()).timeout(Duration.ofHours(1))
                    .build();
        }
        else if (protocol == ProviderProtocol.ANTHROPIC) {
            model = AnthropicStreamingChatModel.builder().apiKey(providerConfig.getApiKey())
                    .baseUrl(providerConfig.getBaseUrl()).version("2023-06-01").modelName(providerConfig.getModel())
                    .temperature(llmModelConfig.getTemperature()).topP(llmModelConfig.getTopP())
                    .maxTokens(llmModelConfig.getMaxTokens())
                    .timeout(Duration.ofHours(1))
                    .build();
        }

        return model;
    }
}
