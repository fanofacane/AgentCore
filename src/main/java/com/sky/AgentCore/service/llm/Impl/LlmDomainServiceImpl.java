package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderAggregate;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.mapper.ModelsMapper;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.LLMDomainService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LlmDomainServiceImpl extends ServiceImpl<ModelsMapper,ModelEntity> implements LLMDomainService {
    @Resource
    private ProvidersMapper providersMapper;
    /** 获取模型
     * @param modelId 模型id */
    public ModelEntity selectModelById(String modelId) {
        ModelEntity modelEntity = lambdaQuery().eq(ModelEntity::getModelId, modelId).one();
        if (modelEntity == null) {
            throw new BusinessException("模型不存在");
        }
        return modelEntity;
    }

    public ProviderEntity getProvider(String providerId) {
        Wrapper<ProviderEntity> wrapper = Wrappers.<ProviderEntity>lambdaQuery().eq(ProviderEntity::getId, providerId);
        ProviderEntity provider = providersMapper.selectOne(wrapper);
        System.out.println("服务商"+ provider);
        if (provider == null) {
            throw new BusinessException("服务商不存在");
        }
        return provider;
    }

    @Override
    public List<ModelEntity> selectList(List<String> ids) {
        return lambdaQuery().in(ModelEntity::getProviderId, ids).list();
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
        List<ModelEntity> list = lambdaQuery().in(ModelEntity::getProviderId, providerIds).list();
        Wrapper<ModelEntity> modelWrapper = Wrappers.<ModelEntity>lambdaQuery().in(ModelEntity::getProviderId,
                providerIds);

        // 按服务商分组
        Map<String, List<ModelEntity>> modelMap = list.stream()
                .collect(Collectors.groupingBy(ModelEntity::getProviderId));

        // 构建聚合根
        return providers.stream().map(provider -> {
            List<ModelEntity> models = modelMap.getOrDefault(provider.getId(), new ArrayList<>());
            return new ProviderAggregate(provider, models);
        }).collect(Collectors.toList());
    }
}
