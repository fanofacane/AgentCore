package com.sky.AgentCore.service.llm;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;

import java.util.List;


public interface LLMDomainService extends IService<ModelEntity> {
    ModelEntity selectModelById(String finalModelId);

    ProviderEntity getProvider(String providerId);

    List<ModelEntity> selectList( List<String> ids);
}
