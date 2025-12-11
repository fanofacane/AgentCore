package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.enums.ModelType;
import com.sky.AgentCore.enums.ProviderProtocol;
import com.sky.AgentCore.enums.ProviderType;

import java.util.List;

public interface LLMAppService extends IService<ProviderEntity> {
    List<ModelDTO> getActiveModelsByType(ProviderType providerType, String userId, ModelType modelType);

    ModelDTO getDefaultModel(String userId);

    ModelEntity getModelById(String modelId);

    ProviderEntity getProvider(String providerId);

    List<ProviderDTO> getProvidersByType(ProviderType providerType, String userId);

    ProviderDTO getProviderDetail(String providerId, String userId);

    ProviderDTO createProvider(ProviderCreateRequest providerCreateRequest, String userId);

    ProviderDTO updateProvider(ProviderUpdateRequest providerUpdateRequest, String userId);

    void updateProviderStatus(String providerId, String userId);

    List<ProviderProtocol> getUserProviderProtocols();

}
