package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.enums.Operator;
import com.sky.AgentCore.enums.ProviderType;

import java.util.List;
import java.util.Set;


public interface LLMDomainService extends IService<ModelEntity> {
    ModelEntity selectModelById(String finalModelId);

    ProviderEntity getProvider(String providerId);

    List<ModelEntity> selectList( List<String> ids);

    List<ProviderEntity> getProvidersByType(ProviderType providerType, String userId);

    ProviderAggregate getProviderAggregate(String providerId, String userId);

    ModelDTO createModel(ModelCreateRequest modelCreateRequest, String userId);

    ModelDTO updateModel(ModelUpdateRequest modelUpdateRequest, String userId);

    void deleteModel(String modelId, String userId, Operator  operator);

    void updateModelStatus(String modelId, String userId);

    List<ModelEntity> getModelsByIds(Set<String> modelIds);
}
