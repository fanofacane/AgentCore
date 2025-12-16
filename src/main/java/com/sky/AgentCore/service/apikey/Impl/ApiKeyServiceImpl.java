package com.sky.AgentCore.service.apikey.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ApiKeyAssembler;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.apiKey.ApiKeyDTO;
import com.sky.AgentCore.dto.apiKey.ApiKeyEntity;
import com.sky.AgentCore.dto.apiKey.QueryApiKeyRequest;
import com.sky.AgentCore.mapper.ApiKeyMapper;
import com.sky.AgentCore.service.agent.AgentAppService;
import com.sky.AgentCore.service.apikey.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** API密钥应用服务 */
@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKeyEntity> implements ApiKeyService {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

    @Autowired
    private AgentAppService agentAppService;
    @Autowired
    private ApiKeyMapper apiKeyMapper;
    /** 创建API密钥
     * @param agentId Agent ID
     * @param name API密钥名称
     * @param userId 用户ID
     * @return 创建的API密钥DTO */
    @Override
    public ApiKeyDTO createApiKey(String agentId, String name, String userId) {
        // 验证Agent是否存在且属于当前用户
        AgentEntity agent = getAgent(agentId, userId);
        if (agent == null) throw new BusinessException("Agent不存在或无权限访问");

        // 创建API密钥实体
        ApiKeyEntity apiKeyEntity = new ApiKeyEntity();
        apiKeyEntity.setAgentId(agentId);
        apiKeyEntity.setUserId(userId);
        apiKeyEntity.setName(name);

        // 调用领域服务创建
        ApiKeyEntity createdApiKey = saveApiKey(apiKeyEntity);

        // 转换为DTO并设置Agent名称
        ApiKeyDTO dto = ApiKeyAssembler.toDTO(createdApiKey);
        dto.setAgentName(agent.getName());

        logger.info("用户 {} 为Agent {} 创建了API密钥: {}", userId, agentId, createdApiKey.getId());

        return dto;
    }

    @Override
    public List<ApiKeyDTO> getUserApiKeys(String userId, QueryApiKeyRequest queryRequest) {
        List<ApiKeyEntity> apiKeys = getUserApiKeyList(userId, queryRequest);
        List<ApiKeyDTO> dtos = ApiKeyAssembler.toDTOs(apiKeys);
        // 批量获取Agent信息
        List<String> agentIds = apiKeys.stream().map(ApiKeyEntity::getAgentId).distinct().collect(Collectors.toList());

        if (!agentIds.isEmpty()) {
            Map<String, AgentEntity> agentMap = agentAppService.getAgentsByIds(agentIds).stream()
                    .collect(Collectors.toMap(AgentEntity::getId, Function.identity()));

            // 设置Agent名称
            dtos.forEach(dto -> {
                AgentEntity agent = agentMap.get(dto.getAgentId());
                if (agent != null) {
                    dto.setAgentName(agent.getName());
                }
            });
        }

        return dtos;
    }
    /** 获取Agent的API密钥列表
     *
     * @param agentId Agent ID
     * @param userId 用户ID
     * @return API密钥列表 */
    @Override
    public List<ApiKeyDTO> getAgentApiKeys(String agentId, String userId) {
        // 验证Agent权限
        AgentEntity agent = getAgent(agentId, userId);
        if (agent == null) throw new BusinessException("Agent不存在或无权限访问");

        List<ApiKeyEntity> apiKeys = getAgentApiKeyList(agentId, userId);
        List<ApiKeyDTO> dtos = ApiKeyAssembler.toDTOs(apiKeys);

        // 设置Agent名称
        dtos.forEach(dto -> dto.setAgentName(agent.getName()));

        return dtos;
    }
    /** 获取API密钥详情
     *
     * @param apiKeyId API密钥ID
     * @param userId 用户ID
     * @return API密钥实体 */
    @Override
    public ApiKeyDTO getApiKey(String apiKeyId, String userId) {
        ApiKeyEntity apiKey = lambdaQuery().eq(ApiKeyEntity::getId, apiKeyId)
                .eq(ApiKeyEntity::getUserId, userId).one();
        if (apiKey == null) throw new BusinessException("API密钥不存在: " + apiKeyId);
        ApiKeyDTO dto = ApiKeyAssembler.toDTO(apiKey);
        // 设置Agent名称
        AgentEntity agent = getAgent(apiKey.getAgentId(), userId);
        if (agent != null) {
            dto.setAgentName(agent.getName());
        }
        return dto;
    }

    @Override
    public void deleteApiKey(String apiKeyId, String userId) {
        boolean success = lambdaUpdate().eq(ApiKeyEntity::getId, apiKeyId).eq(ApiKeyEntity::getUserId, userId)
                .remove();
        if (!success) throw new BusinessException("删除API密钥失败");
    }

    @Override
    public ApiKeyDTO resetApiKey(String apiKeyId, String userId) {
        ApiKeyEntity resetApiKey = resetApiKeys(apiKeyId, userId);
        ApiKeyDTO dto = ApiKeyAssembler.toDTO(resetApiKey);

        // 设置Agent名称
        AgentEntity agent = getAgent(resetApiKey.getAgentId(), userId);
        if (agent != null) dto.setAgentName(agent.getName());

        logger.info("用户 {} 重置了API密钥: {}", userId, apiKeyId);
        return dto;
    }

    /** 验证API Key
     * @param apiKey API密钥
     * @return API密钥实体，如果无效则抛出异常 */
    @Override
    public ApiKeyEntity validateApiKey(String apiKey) {
        ApiKeyEntity apiKeyEntity = lambdaQuery().eq(ApiKeyEntity::getApiKey, apiKey).one();

        if (apiKeyEntity == null) {
            throw new BusinessException("无效的API Key");
        }

        if (!apiKeyEntity.isAvailable()) {
            throw new BusinessException("API Key已禁用或过期");
        }

        return apiKeyEntity;
    }

    /** 更新API Key使用记录
     * @param apiKey API密钥 */
    @Override
    public void updateUsage(String apiKey) {
        boolean update = lambdaUpdate().eq(ApiKeyEntity::getApiKey, apiKey).setSql("usage_count = usage_count + 1")
                .set(ApiKeyEntity::getLastUsedAt, LocalDateTime.now()).update();
        if (!update) throw new BusinessException("更新API密钥使用记录失败");
    }

    /** 重置API密钥
     *
     * @param apiKeyId API密钥ID
     * @param userId 用户ID
     * @return 新的API密钥实体 */
    public ApiKeyEntity resetApiKeys(String apiKeyId, String userId) {
        ApiKeyEntity apiKeyEntity = lambdaQuery().eq(ApiKeyEntity::getId, apiKeyId)
                .eq(ApiKeyEntity::getUserId, userId).one();

        // 生成新的API Key
        apiKeyEntity.generateApiKey();
        apiKeyEntity.setUsageCount(0);
        apiKeyEntity.setLastUsedAt(null);

        boolean update = lambdaUpdate().eq(ApiKeyEntity::getId, apiKeyId).eq(ApiKeyEntity::getUserId, userId)
                .update(apiKeyEntity);
        if (!update) throw new BusinessException("更新API密钥失败");
        return apiKeyEntity;
    }

    private List<ApiKeyEntity> getAgentApiKeyList(String agentId, String userId) {
        return lambdaQuery().eq(ApiKeyEntity::getId,agentId)
                .eq(ApiKeyEntity::getUserId,userId).orderByDesc(ApiKeyEntity::getCreatedAt).list();
    }

    /** 获取用户的API密钥列表
     * @param userId 用户ID
     * @param queryRequest 查询条件
     * @return API密钥列表 */
    private List<ApiKeyEntity> getUserApiKeyList(String userId, QueryApiKeyRequest queryRequest) {
        LambdaQueryWrapper<ApiKeyEntity> wrapper = Wrappers.<ApiKeyEntity>lambdaQuery().eq(ApiKeyEntity::getUserId,
                userId);

        // 添加查询条件
        if (queryRequest != null) {
            // 名称模糊查询
            if (StringUtils.hasText(queryRequest.getName())) {
                wrapper.like(ApiKeyEntity::getName, queryRequest.getName().trim());
            }

            // 状态筛选
            if (queryRequest.getStatus() != null) {
                wrapper.eq(ApiKeyEntity::getStatus, queryRequest.getStatus());
            }

            // Agent ID 筛选
            if (StringUtils.hasText(queryRequest.getAgentId())) {
                wrapper.eq(ApiKeyEntity::getAgentId, queryRequest.getAgentId());
            }
        }

        wrapper.orderByDesc(ApiKeyEntity::getCreatedAt);
        return apiKeyMapper.selectList(wrapper);
    }

    /** 创建API密钥
     *
     * @param apiKeyEntity API密钥实体
     * @return 创建后的API密钥实体 */
    private ApiKeyEntity saveApiKey(ApiKeyEntity apiKeyEntity) {
        // 生成API Key
        apiKeyEntity.generateApiKey();

        // 保存API密钥
        save(apiKeyEntity);

        return apiKeyEntity;
    }

    private AgentEntity getAgent(String agentId, String userId) {
        return agentAppService.lambdaQuery().eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, userId).one();
    }

}
