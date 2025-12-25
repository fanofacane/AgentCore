package com.sky.AgentCore.dto.agent;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import lombok.Data;


public class AgentVO extends AgentDTO{
    private LLMModelConfig llmModelConfig;

    public LLMModelConfig getLlmModelConfig() {
        return llmModelConfig;
    }

    public void setLlmModelConfig(LLMModelConfig llmModelConfig) {
        this.llmModelConfig = llmModelConfig;
    }

}
