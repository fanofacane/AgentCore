package com.sky.AgentCore.service.llm.Impl;

import com.sky.AgentCore.config.Factory.LLMProviderFactory;
import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.dto.enums.ProviderProtocol;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

public class LLMProviderService {

    public static ChatModel getStrand(ProviderProtocol protocol, ProviderConfig providerConfig) {
        return LLMProviderFactory.getLLMProvider(protocol, providerConfig);
    }

    public static StreamingChatModel getStream(ProviderProtocol protocol, ProviderConfig providerConfig, LLMModelConfig llmModelConfig) {
        return LLMProviderFactory.getLLMProviderByStream(protocol, providerConfig,llmModelConfig);
    }
}
