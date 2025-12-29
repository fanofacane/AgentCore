package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ProviderCreateRequest;
import com.sky.AgentCore.dto.model.ProviderDTO;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.enums.ModelType;
import com.sky.AgentCore.dto.enums.ProviderProtocol;

import java.util.List;

public interface AdminLLMAppService extends IService<ProviderEntity> {
    List<ProviderDTO> getOfficialProviders(String userId, Integer page, Integer pageSize);

    ProviderDTO getProviderDetail(String providerId, String userId);

    ProviderDTO createProvider(ProviderCreateRequest request, String userId);

    List<ProviderProtocol> getProviderProtocols();

    List<ModelDTO> getOfficialModels(String userId, String providerId, ModelType type, Integer page, Integer pageSize);

    List<ModelType> getModelTypes();
}
