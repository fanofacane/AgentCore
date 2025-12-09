package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.converter.ModelAssembler;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderAggregate;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.enums.ModelType;
import com.sky.AgentCore.enums.ProviderType;
import com.sky.AgentCore.mapper.ModelsMapper;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.LLMAppService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class LLMAppServiceImpl extends ServiceImpl<ProvidersMapper, ProviderEntity> implements LLMAppService {
    @Autowired
    private ModelsMapper modelsMapper;
    @Autowired
    private UserSettingsDomainService userSettingsService;
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
                wrapper.eq(ProviderEntity::getIsOfficial, 1);
                break;
            case CUSTOM :
                wrapper.eq(ProviderEntity::getUserId, userId).eq(ProviderEntity::getIsOfficial, 0);
                break;
            case ALL :
            default :
                wrapper.eq(ProviderEntity::getUserId, userId).or().eq(ProviderEntity::getIsOfficial, 1);
        }
        System.out.println("查询服务商列表"+list(wrapper));
        List<ProviderAggregate> providerAggregates = buildProviderAggregatesWithActiveModels(this.list(wrapper));
        return providerAggregates.stream().filter(ProviderAggregate::getStatus)
                .flatMap(provider -> provider.getModels().stream()
                        .filter(model -> modelType == null || model.getType() == modelType)
                        .map(model -> ModelAssembler.toDTO(model, provider.getName())))
                .collect(Collectors.toList());
    }

    @Override
    public ModelDTO getDefaultModel(String userId) {
        String defaultModelId = userSettingsService.getUserDefaultModelId(userId);
        ModelEntity modelEntity = modelsMapper.selectById(defaultModelId);
        return ModelAssembler.toDTO(modelEntity);
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
                .in(ModelEntity::getProviderId, providerIds).eq(ModelEntity::getStatus, 1));
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
