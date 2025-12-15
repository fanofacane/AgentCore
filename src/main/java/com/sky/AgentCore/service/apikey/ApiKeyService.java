package com.sky.AgentCore.service.apikey;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.apiKey.ApiKeyDTO;
import com.sky.AgentCore.dto.apiKey.ApiKeyEntity;
import com.sky.AgentCore.dto.apiKey.QueryApiKeyRequest;

import java.util.List;

public interface ApiKeyService extends IService<ApiKeyEntity> {
    ApiKeyDTO createApiKey(String agentId, String name, String userId);

    List<ApiKeyDTO> getUserApiKeys(String userId, QueryApiKeyRequest queryRequest);

    List<ApiKeyDTO> getAgentApiKeys(String agentId, String userId);

    ApiKeyDTO getApiKey(String apiKeyId, String userId);

    void deleteApiKey(String apiKeyId, String userId);

    ApiKeyDTO resetApiKey(String apiKeyId, String userId);
}
