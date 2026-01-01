package com.sky.AgentCore.controller.agent;

import com.sky.AgentCore.dto.apiKey.*;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.user.ApiKeyService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/** API密钥管理控制器 */
@RestController
@RequestMapping("/api-keys")
public class PortalApiKeyController {
    @Autowired
    private ApiKeyService apiKeyService;
    /** 创建API密钥
     * @param request 创建请求
     * @return 创建的API密钥 */
    @PostMapping
    public Result<ApiKeyResponse> createApiKey(@RequestBody @Validated CreateApiKeyRequest request) {
        String userId = UserContext.getCurrentUserId();
        ApiKeyDTO apiKeyDTO = apiKeyService.createApiKey(request.getAgentId(), request.getName(), userId);

        ApiKeyResponse response = new ApiKeyResponse();
        BeanUtils.copyProperties(apiKeyDTO, response);

        return Result.success(response);
    }
    /** 获取用户的API密钥列表
     *
     * @param queryRequest 查询条件
     * @return API密钥列表 */
    @GetMapping
    public Result<List<ApiKeyResponse>> getUserApiKeys(QueryApiKeyRequest queryRequest) {
        String userId = UserContext.getCurrentUserId();
        List<ApiKeyDTO> apiKeys = apiKeyService.getUserApiKeys(userId, queryRequest);

        List<ApiKeyResponse> responses = apiKeys.stream().map(dto -> {
            ApiKeyResponse response = new ApiKeyResponse();
            BeanUtils.copyProperties(dto, response);
            return response;
        }).collect(Collectors.toList());

        return Result.success(responses);
    }
    /** 获取Agent的API密钥列表
     *
     * @param agentId Agent ID
     * @return API密钥列表 */
    @GetMapping("/agent/{agentId}")
    public Result<List<ApiKeyResponse>> getAgentApiKeys(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        List<ApiKeyDTO> apiKeys = apiKeyService.getAgentApiKeys(agentId, userId);

        List<ApiKeyResponse> responses = apiKeys.stream().map(dto -> {
            ApiKeyResponse response = new ApiKeyResponse();
            BeanUtils.copyProperties(dto, response);
            return response;
        }).collect(Collectors.toList());

        return Result.success(responses);
    }
    /** 获取API密钥详情
     *
     * @param apiKeyId API密钥ID
     * @return API密钥详情 */
    @GetMapping("/{apiKeyId}")
    public Result<ApiKeyResponse> getApiKey(@PathVariable String apiKeyId) {
        String userId = UserContext.getCurrentUserId();
        ApiKeyDTO apiKeyDTO = apiKeyService.getApiKey(apiKeyId, userId);

        ApiKeyResponse response = new ApiKeyResponse();
        BeanUtils.copyProperties(apiKeyDTO, response);

        return Result.success(response);
    }
    /** 删除API密钥
     *
     * @param apiKeyId API密钥ID
     * @return 操作结果 */
    @DeleteMapping("/{apiKeyId}")
    public Result<Void> deleteApiKey(@PathVariable String apiKeyId) {
        String userId = UserContext.getCurrentUserId();
        apiKeyService.deleteApiKey(apiKeyId, userId);
        return Result.success();
    }
    /** 重置API密钥
     *
     * @param apiKeyId API密钥ID
     * @return 新的API密钥 */
    @PostMapping("/{apiKeyId}/reset")
    public Result<ApiKeyResponse> resetApiKey(@PathVariable String apiKeyId) {
        String userId = UserContext.getCurrentUserId();
        ApiKeyDTO apiKeyDTO = apiKeyService.resetApiKey(apiKeyId, userId);

        ApiKeyResponse response = new ApiKeyResponse();
        BeanUtils.copyProperties(apiKeyDTO, response);

        return Result.success(response);
    }
}
