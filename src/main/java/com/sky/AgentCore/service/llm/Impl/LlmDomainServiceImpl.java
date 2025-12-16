package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ModelAssembler;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.enums.Operator;
import com.sky.AgentCore.enums.ProviderType;
import com.sky.AgentCore.mapper.ModelsMapper;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LlmDomainServiceImpl extends ServiceImpl<ModelsMapper,ModelEntity> implements LLMDomainService {
    @Resource
    private ProvidersMapper providersMapper;
    @Autowired
    private UserSettingsDomainService userSettingsDomainService;
    @Autowired
    private ModelsMapper modelsMapper;
    /** 获取模型
     * @param modelId 模型id */
    public ModelEntity selectModelById(String modelId) {
        ModelEntity modelEntity = lambdaQuery().eq(ModelEntity::getId, modelId).one();
        if (modelEntity == null) {
            throw new BusinessException("模型不存在");
        }
        return modelEntity;
    }

    public ProviderEntity getProvider(String providerId) {
        Wrapper<ProviderEntity> wrapper = Wrappers.<ProviderEntity>lambdaQuery().eq(ProviderEntity::getId, providerId);
        ProviderEntity provider = providersMapper.selectOne(wrapper);
        if (provider == null) {
            throw new BusinessException("服务商不存在");
        }
        return provider;
    }

    @Override
    public List<ModelEntity> selectList(List<String> ids) {
        return lambdaQuery().in(ModelEntity::getProviderId, ids).list();
    }

    @Override
    public List<ProviderEntity> getProvidersByType(ProviderType providerType, String userId) {
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

        return providersMapper.selectList(wrapper);
    }

    @Override
    public ProviderAggregate getProviderAggregate(String providerId, String userId) {
        // 获取服务商
        ProviderEntity provider = getProviderByUserIdAndProviderId(providerId, userId);
        // 获取服务商下的激活模型列表
        List<ModelEntity> modelList = getActiveModelList(providerId, userId);

        return new ProviderAggregate(provider, modelList);
    }
    /** 创建模型
     * @param modelCreateRequest 请求对象
     * @param userId 用户id
     * @return ModelDTO */
    @Override
    public ModelDTO createModel(ModelCreateRequest modelCreateRequest, String userId) {
        ModelEntity model = ModelAssembler.toEntity(modelCreateRequest, userId);
        // 用户创建默认是非官方
        model.setIsOfficial(false);
        checkProviderExists(modelCreateRequest.getProviderId(), userId);
        insertModel(model);
        String userDefaultModelId = userSettingsDomainService.getUserDefaultModelId(userId);
        // 如果用户没有默认模型则设置当前模型
        if (userDefaultModelId == null) {
            userSettingsDomainService.setUserDefaultModelId(userId, model.getModelId());
        }
        return ModelAssembler.toDTO(model);
    }

    @Override
    public ModelDTO updateModel(ModelUpdateRequest modelUpdateRequest, String userId) {
        ModelEntity entity = ModelAssembler.toEntity(modelUpdateRequest, userId);
        lambdaUpdate().eq(ModelEntity::getId, modelUpdateRequest.getId())
                .eq(ModelEntity::getUserId, userId).update(entity);
        return ModelAssembler.toDTO(entity);
    }

    @Override
    public void deleteModel(String modelId, String userId, Operator operator) {
        lambdaUpdate().eq(ModelEntity::getId, modelId)
                .eq(operator.needCheckUserId(), ModelEntity::getUserId, userId).remove();
        // todo 发布模型删除事件
        //  eventPublisher.publishEvent(new ModelDeletedEvent(modelId, userId));
    }

    @Override
    public void updateModelStatus(String modelId, String userId) {
        ModelEntity model = getById(modelId);
        boolean newStatus = !model.getStatus();
        lambdaUpdate().eq(ModelEntity::getId, modelId)
                .eq(ModelEntity::getUserId, userId).setSql("status = NOT status")
                .update();
        // todo 发布模型更新事件
        //  获取更新后的模型信息
        //ModelEntity updatedModel = getById(modelId);
        // 发布模型状态变更事件
        //eventPublisher.publishEvent(new ModelStatusChangedEvent(modelId, userId, updatedModel, newStatus, ""));

    }

    @Override
    public List<ModelEntity> getModelsByIds(Set<String> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) return new ArrayList<>();
        return lambdaQuery().in(ModelEntity::getId, modelIds).list();
    }

    @Override
    public boolean canUserUseModel(String modelId, String userId) {
        return lambdaQuery().eq(ModelEntity::getModelId, modelId).eq(ModelEntity::getStatus, true)
                .and(q -> q.eq(ModelEntity::getUserId, userId).or().eq(ModelEntity::getIsOfficial, true))
                .exists();
    }

    @Override
    public void deleteProvider(String providerId, String userId, Operator operator) {
        // 删除服务商前先获取要删除的模型列表，用于发布批量删除事件
        Wrapper<ModelEntity> modelQueryWrapper = Wrappers.<ModelEntity>lambdaQuery().eq(ModelEntity::getProviderId,
                providerId);
        List<ModelEntity> modelsToDelete = modelsMapper.selectList(modelQueryWrapper);

        Wrapper<ProviderEntity> wrapper = Wrappers.<ProviderEntity>lambdaQuery().eq(ProviderEntity::getId, providerId)
                .eq(operator.needCheckUserId(), ProviderEntity::getUserId, userId);
        int affected = providersMapper.delete(wrapper);
        if (affected == 0) throw new BusinessException("数据更新失败");

        // 删除模型
        Wrapper<ModelEntity> modelWrapper = Wrappers.<ModelEntity>lambdaQuery().eq(ModelEntity::getProviderId,
                providerId);
        int delete = modelsMapper.delete(modelWrapper);

        // todo 如果有模型被删除，发布批量删除事件
/*        if (delete > 0) {
            List<ModelsBatchDeletedEvent.ModelDeleteItem> deleteItems = modelsToDelete.stream()
                    .map(model -> new ModelsBatchDeletedEvent.ModelDeleteItem(model.getId(), model.getUserId()))
                    .collect(Collectors.toList());

            eventPublisher.publishEvent(new ModelsBatchDeletedEvent(deleteItems, userId));
        }*/
    }

    private void insertModel(ModelEntity model) {
        save(model);
        // todo 发布模型创建事件
        // eventPublisher.publishEvent(new ModelCreatedEvent(model.getId(), model.getUserId(), model));
    }

    // 检查服务商是否存在
    public void checkProviderExists(String providerId, String userId) {
        Wrapper<ProviderEntity> wrapper = Wrappers.<ProviderEntity>lambdaQuery().eq(ProviderEntity::getId, providerId)
                .eq(ProviderEntity::getUserId, userId);
        ProviderEntity provider = providersMapper.selectOne(wrapper);
        if (provider == null) {
            throw new BusinessException("服务商不存在");
        }
    }
    /** 获取激活的模型列表
     * @param providerId 服务商ID
     * @param userId 用户ID
     * @return 激活的模型列表 */
    public List<ModelEntity> getActiveModelList(String providerId, String userId) {
        return lambdaQuery().eq(ModelEntity::getProviderId, providerId).list();
    }
    private ProviderEntity getProviderByUserIdAndProviderId(String providerId, String userId) {

        Wrapper<ProviderEntity> wrapper = Wrappers.<ProviderEntity>lambdaQuery().eq(ProviderEntity::getId, providerId);
        ProviderEntity provider = providersMapper.selectOne(wrapper);

        if (provider == null) throw new BusinessException("服务商不存在");
        return provider;
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
