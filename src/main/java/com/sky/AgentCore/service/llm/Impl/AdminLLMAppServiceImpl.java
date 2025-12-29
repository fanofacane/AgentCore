package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.ModelAssembler;
import com.sky.AgentCore.converter.assembler.ProviderAssembler;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.dto.enums.ModelType;
import com.sky.AgentCore.dto.enums.ProviderProtocol;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.AdminLLMAppService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminLLMAppServiceImpl extends ServiceImpl<ProvidersMapper, ProviderEntity> implements AdminLLMAppService {
    @Autowired
    private LLMDomainService llmDomainService;
    @Override
    public List<ProviderDTO> getOfficialProviders(String userId, Integer page, Integer pageSize) {
        return lambdaQuery().eq(ProviderEntity::getIsOfficial, true)
                .list().stream().map(ProviderAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderDTO getProviderDetail(String providerId, String userId) {
        ProviderEntity provider = lambdaQuery().eq(ProviderEntity::getId, providerId)
                .eq(ProviderEntity::getUserId, userId).one();
        if (provider == null) throw new BusinessException("服务商不存在");
        List<ModelEntity> modelList = llmDomainService.lambdaQuery().eq(ModelEntity::getProviderId, providerId)
                .eq(ModelEntity::getUserId, userId).eq(ModelEntity::getIsOfficial, true).list();
        ProviderAggregate providerAggregate = new ProviderAggregate(provider, modelList);
        return ProviderAssembler.toDTO(providerAggregate);
    }

    @Override
    public ProviderDTO createProvider(ProviderCreateRequest request, String userId) {
        ProviderEntity provider = ProviderAssembler.toEntity(request, userId);
        provider.setIsOfficial(true);
        save(provider);
        return ProviderAssembler.toDTO(provider);
    }

    @Override
    public List<ProviderProtocol> getProviderProtocols() {
        return Arrays.asList(ProviderProtocol.values());
    }

    @Override
    public List<ModelDTO> getOfficialModels(String userId, String providerId, ModelType modelType, Integer page, Integer pageSize) {
        // 查询官方模型，显示所有状态的模型（不过滤状态）
        List<ProviderEntity> providerEntities = lambdaQuery().eq(ProviderEntity::getIsOfficial, true).list();
        List<ProviderAggregate> providerAggregates = buildProviderAggregatesWithAllModels(providerEntities);
        return providerAggregates.stream()
                .flatMap(provider -> provider.getModels().stream()
                        .filter(model -> modelType == null || model.getType() == modelType) // 按类型过滤
                        .filter(model -> providerId == null || provider.getId().equals(providerId))) // 按服务商过滤
                .map(ModelAssembler::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ModelType> getModelTypes() {
        return Arrays.asList(ModelType.values());
    }

    /** 构建包含所有模型的服务商聚合根 - 用于管理员功能
     * @param providers 服务商列表
     * @return 服务商聚合根列表 */
    private List<ProviderAggregate> buildProviderAggregatesWithAllModels(List<ProviderEntity> providers) {
        if (providers.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有服务商ID
        List<String> providerIds = providers.stream().map(ProviderEntity::getId).collect(Collectors.toList());

        // 批量查询所有模型（不过滤状态）
        List<ModelEntity> allModels = llmDomainService.selectList(providerIds);

        // 按服务商分组
        Map<String, List<ModelEntity>> modelMap = allModels.stream()
                .collect(Collectors.groupingBy(ModelEntity::getProviderId));

        // 构建聚合根
        return providers.stream().map(provider -> {
            List<ModelEntity> models = modelMap.getOrDefault(provider.getId(), new ArrayList<>());
            return new ProviderAggregate(provider, models);
        }).collect(Collectors.toList());
    }
}
