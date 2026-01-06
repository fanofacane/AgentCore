package com.sky.AgentCore.service.llm.provider;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ProviderConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

public interface Provider {

    ProviderProtocol getProtocol();

    ChatModel createChatModel(ProviderConfig providerConfig);

    StreamingChatModel createStreamingChatModel(ProviderConfig providerConfig, LLMModelConfig llmModelConfig);
}

