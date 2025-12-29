package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.ModelAssembler;
import com.sky.AgentCore.converter.assembler.ProviderAssembler;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.dto.enums.ModelType;
import com.sky.AgentCore.dto.enums.Operator;
import com.sky.AgentCore.dto.enums.ProviderProtocol;
import com.sky.AgentCore.dto.enums.ProviderType;
import com.sky.AgentCore.mapper.ModelsMapper;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.LLMAppService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class LLMAppServiceImpl extends ServiceImpl<ProvidersMapper, ProviderEntity> implements LLMAppService {
    @Autowired
    private ModelsMapper modelsMapper;
    @Autowired
    private UserSettingsDomainService userSettingsService;
    @Autowired
    private LLMDomainService llmDomainService;
    @Override
    /** 获取所有激活模型
     * @param providerType 服务商类型
     * @param userId 用户id
     * @param modelType 模型类型（可选）
     * @return 模型列表 */
    public List<ModelDTO> getActiveModelsByType(ProviderType providerType, String userId, ModelType modelType) {
        LambdaQueryWrapper<ProviderEntity> wrapper = Wrappers.lambdaQuery();
        switch (providerType) {
            case OFFICIAL :
                wrapper.eq(ProviderEntity::getIsOfficial, true);
                break;
            case CUSTOM :
                wrapper.eq(ProviderEntity::getUserId, userId).eq(ProviderEntity::getIsOfficial, false);
                break;
            case ALL :
            default :
               wrapper.eq(ProviderEntity::getUserId, userId).or().eq(ProviderEntity::getIsOfficial, true);
        }
        List<ProviderAggregate> providerAggregates = buildProviderAggregatesWithActiveModels(this.list(wrapper));
        return providerAggregates.stream().filter(ProviderAggregate::getStatus)
                .flatMap(provider -> provider.getModels().stream()
                        .filter(model -> modelType == null || model.getType() == modelType)
                        .filter(ModelEntity::getStatus)
                        .map(model -> ModelAssembler.toDTO(model, provider.getName())))
                .collect(Collectors.toList());
    }

    @Override
    public ModelDTO getDefaultModel(String userId) {
        String defaultModelId = userSettingsService.getUserDefaultModelId(userId);
        ModelEntity modelEntity = modelsMapper.selectById(defaultModelId);
        return ModelAssembler.toDTO(modelEntity);
    }

    @Override
    public ModelEntity getModelById(String modelId) {
        ModelEntity modelEntity = modelsMapper.selectById(modelId);
        if (modelEntity == null) {
            throw new BusinessException("模型不存在");
        }
        return modelEntity;
    }

    @Override
    public ProviderEntity getProvider(String providerId) {

        ProviderEntity provider = getById(providerId);
        if (provider == null) {
            throw new BusinessException("服务商不存在");
        }
        return provider;
    }
    private ProviderEntity getProviderByUserIdAndProviderId(String providerId, String userId) {
        ProviderEntity provider = lambdaQuery().eq(ProviderEntity::getId, providerId)
                .eq(ProviderEntity::getUserId, userId).one();
        if (provider == null) throw new BusinessException("服务商不存在");
        return provider;
    }

    @Override
    public List<ProviderDTO> getProvidersByType(ProviderType providerType, String userId) {
        List<ProviderEntity> providers = llmDomainService.getProvidersByType(providerType, userId);
        List<ProviderAggregate> providerAggregates = buildProviderAggregatesWithActiveModels(providers);
        return providerAggregates.stream().map(ProviderAssembler::toDTO).collect(Collectors.toList());
    }
    /** 获取服务商聚合根
     * @param providerId 服务商id
     * @param userId 用户id
     * @return ProviderAggregate */
    @Override
    public ProviderDTO getProviderDetail(String providerId, String userId) {
        ProviderAggregate providerAggregate = llmDomainService.getProviderAggregate(providerId, userId);
        return ProviderAssembler.toDTO(providerAggregate);
    }
    /** 创建服务商
     * @param providerCreateRequest 请求对象
     * @param userId 用户id
     * @return ProviderDTO */
    @Override
    public ProviderDTO createProvider(ProviderCreateRequest providerCreateRequest, String userId) {
        ProviderEntity provider = ProviderAssembler.toEntity(providerCreateRequest, userId);
        provider.setIsOfficial(false);
        validateProviderProtocol(provider.getProtocol());
        save(provider);
        return ProviderAssembler.toDTO(provider);
    }

    @Override
    public ProviderDTO updateProvider(ProviderUpdateRequest providerUpdateRequest, String userId) {
        // 先获取当前服务商数据
        ProviderEntity existingProvider = getProviderByUserIdAndProviderId(providerUpdateRequest.getId(), userId);

        // 判断是否需要保留原有的密钥
        if (providerUpdateRequest.getConfig() != null && providerUpdateRequest.getConfig().getApiKey() != null
                && providerUpdateRequest.getConfig().getApiKey().matches("\\*+")) {
            // 如果传入的是掩码，使用原有的密钥
            providerUpdateRequest.getConfig().setApiKey(existingProvider.getConfig().getApiKey());
        }

        ProviderEntity provider = ProviderAssembler.toEntity(providerUpdateRequest, userId);
        validateProviderProtocol(provider.getProtocol());
        lambdaUpdate().eq(ProviderEntity::getId, provider.getId())
                .eq(provider.needCheckUserId(), ProviderEntity::getUserId, provider.getUserId())
                .update(provider);
        return ProviderAssembler.toDTO(provider);
    }

    @Override
    public void updateProviderStatus(String providerId, String userId) {
        // 原子更新：SQL层直接取反，无并发问题
        lambdaUpdate().eq(ProviderEntity::getId, providerId)
                .eq(ProviderEntity::getUserId, userId)
                // 适配MySQL：tinyint/boolean类型取反
                .setSql("status = NOT status")
                .update();
    }

    @Override
    public List<ProviderProtocol> getUserProviderProtocols() {
        return Arrays.asList(ProviderProtocol.values());
    }

    @Override
    public void deleteProvider(String providerId, String userId) {
        llmDomainService.deleteProvider(providerId, userId, Operator.USER);
    }


    /** 验证服务商协议是否支持
     * @param protocol 协议 */
    private void validateProviderProtocol(ProviderProtocol protocol) {
        // TODO: 从配置或枚举中获取支持的服务商协议列表
        if (!isSupportedProvider(protocol)) {
            throw new BusinessException("不支持的服务商协议类型: " + protocol);
        }
    }

    /** 检查是否是支持的服务商协议
     * @param protocol 服务商提供商编码
     * @return */
    private boolean isSupportedProvider(ProviderProtocol protocol) {
        return Arrays.stream(ProviderProtocol.values()).anyMatch(providerType -> providerType == protocol);
    }

    /** 构建服务商聚合根，只包含激活的模型
     * @param providers 服务商列表
     * @return 服务商聚合根列表 */
    private List<ProviderAggregate> buildProviderAggregatesWithActiveModels(List<ProviderEntity> providers) {
        List<ProviderAggregate> providerAggregates = new ArrayList<>();
        if (providers == null || providers.isEmpty()) {
            return providerAggregates;
        }

        // 收集服务商id
        List<String> providerIds = providers.stream().map(ProviderEntity::getId).collect(Collectors.toList());
        // 查询激活的模型
        List<ModelEntity> activeModels = modelsMapper.selectList(Wrappers.<ModelEntity>lambdaQuery()
                .in(ModelEntity::getProviderId, providerIds).eq(ModelEntity::getStatus, true));
        // 转为map，映射关系
        Map<String, List<ModelEntity>> modelMap = activeModels.stream()
                .collect(Collectors.groupingBy(ModelEntity::getProviderId));

        // 遍历服务商，创建聚合根，设置模型
        for (ProviderEntity provider : providers) {
            List<ModelEntity> modelList = modelMap.get(provider.getId());
            ProviderAggregate providerAggregate = new ProviderAggregate(provider, modelList);
            providerAggregates.add(providerAggregate);
        }
        return providerAggregates;
    }
}
