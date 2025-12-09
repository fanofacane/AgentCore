package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.enums.ModelType;
import com.sky.AgentCore.enums.ProviderType;

import java.util.List;

public interface LLMAppService extends IService<ProviderEntity> {
    List<ModelDTO> getActiveModelsByType(ProviderType providerType, String userId, ModelType modelType);

    ModelDTO getDefaultModel(String userId);
}
