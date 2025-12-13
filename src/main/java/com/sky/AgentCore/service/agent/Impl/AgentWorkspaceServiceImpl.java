package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.AgentAssembler;
import com.sky.AgentCore.converter.AgentWorkspaceAssembler;
import com.sky.AgentCore.dto.LLMModelConfig;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;
import com.sky.AgentCore.enums.PublishStatus;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.mapper.AgentVersionMapper;
import com.sky.AgentCore.mapper.AgentWorkspaceMapper;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.llm.LLMAppService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AgentWorkspaceServiceImpl extends ServiceImpl<AgentWorkspaceMapper, AgentWorkspaceEntity> implements AgentWorkspaceService {
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private LLMAppService llmService;
    @Autowired
    private AgentVersionMapper agentVersionMapper;
    @Override
    public List<AgentDTO> getAgents(String userId) {
        List<AgentWorkspaceEntity> list = lambdaQuery()
                .eq(AgentWorkspaceEntity::getUserId, userId)
                .select(AgentWorkspaceEntity::getAgentId).list();
        List<String> agentIds = list.stream()
                .map(AgentWorkspaceEntity::getAgentId).toList();

        if (agentIds.isEmpty()) return Collections.emptyList();

        List<AgentEntity> agentEntities = agentMapper.selectByIds(agentIds);
        return AgentAssembler.toDTOs(agentEntities);
    }
    /** 保存agent的模型配置
     * @param agentId agent ID
     * @param userId 用户ID
     * @param request 模型配置 */
    @Override
    public void updateModelConfig(String agentId, String userId, UpdateModelConfigRequest request) {
        LLMModelConfig llmModelConfig = AgentWorkspaceAssembler.toLLMModelConfig(request);
        String modelId = llmModelConfig.getModelId();

        // 激活校验
        ModelEntity model = llmService.getModelById(modelId);
        model.isActive();
        ProviderEntity provider = llmService.getProvider(model.getProviderId());
        provider.isActive();
        boolean success = lambdaUpdate()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId)
                .update(new AgentWorkspaceEntity(llmModelConfig));
        if (!success) throw new RuntimeException("更新模型配置失败");
    }

    @Override
    public void deleteAgent(String id, String userId) {

    }

    @Override
    public AgentWorkspaceEntity getWorkspace(String agentId, String userId) {

        AgentWorkspaceEntity agentWorkspaceEntity = lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).one();
        if (agentWorkspaceEntity == null) {
            throw new BusinessException("助理不存在");
        }
        return agentWorkspaceEntity;
    }

    @Override
    public List<AgentWorkspaceEntity> listAgents(List<String> agentIds, String userId) {
        return lambdaQuery().eq(AgentWorkspaceEntity::getUserId, userId)
                .in(AgentWorkspaceEntity::getAgentId, agentIds).list();
    }

    @Override
    public LLMModelConfig getConfiguredModelId(String agentId, String userId) {
        AgentWorkspaceEntity config = lambdaQuery().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).one();
        if (config == null) throw new BusinessException("助理不存在");
        return config.getLlmModelConfig();
    }

    @Override
    // 添加到工作区
    public void addAgent(String agentId, String userId) {
        AgentEntity agent = agentMapper.selectById(agentId);
        if (agent.getUserId().equals(userId)) throw new BusinessException("不可添加自己的助理");

        boolean exists = lambdaQuery().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).exists();
        if (exists) throw new BusinessException("不可重复添加助理");

        agent.isEnable();
        String publishedVersion = agent.getPublishedVersion();
        AgentVersionEntity agentVersionEntity = agentVersionMapper.selectById(publishedVersion);
        if (!agentVersionEntity.getPublishStatusEnum().equals(PublishStatus.PUBLISHED)) {
            throw new BusinessException("助理未发布");
        }
        save(new AgentWorkspaceEntity(agentId, userId, new LLMModelConfig()));
    }
}
