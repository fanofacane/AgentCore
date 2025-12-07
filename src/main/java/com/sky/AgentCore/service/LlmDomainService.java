package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;


public interface LlmDomainService extends IService<ModelEntity> {
    ModelEntity selectModelById(String finalModelId);

    ProviderEntity getProvider(String providerId);
}
