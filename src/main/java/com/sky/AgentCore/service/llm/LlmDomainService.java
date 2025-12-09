package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;


public interface LlmDomainService extends IService<ModelEntity> {
    ModelEntity selectModelById(String finalModelId);

    ProviderEntity getProvider(String providerId);
}
