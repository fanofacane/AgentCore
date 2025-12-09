package com.sky.AgentCore.service.llm.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.mapper.ModelsMapper;
import com.sky.AgentCore.mapper.ProvidersMapper;
import com.sky.AgentCore.service.llm.LlmDomainService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class LlmDomainServiceImpl extends ServiceImpl<ModelsMapper,ModelEntity> implements LlmDomainService {
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
        if (provider == null) {
            throw new BusinessException("服务商不存在");
        }
        return provider;
    }
}
