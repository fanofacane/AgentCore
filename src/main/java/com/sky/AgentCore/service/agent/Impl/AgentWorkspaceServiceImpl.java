package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.AgentAssembler;
import com.sky.AgentCore.converter.assembler.AgentWorkspaceAssembler;
import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.dto.enums.PublishStatus;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.mapper.AgentVersionMapper;
import com.sky.AgentCore.mapper.AgentWorkspaceMapper;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.agent.SessionService;
import com.sky.AgentCore.service.chat.MessageService;
import com.sky.AgentCore.service.llm.LLMAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentWorkspaceServiceImpl extends ServiceImpl<AgentWorkspaceMapper, AgentWorkspaceEntity> implements AgentWorkspaceService {
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private LLMAppService llmService;
    @Autowired
    private AgentVersionMapper agentVersionMapper;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private MessageService messageService;
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

    /** 删除工作区中的助理
     * @param agentId 助理id
     * @param userId 用户id */
    @Override
    @Transactional
    public void deleteAgent(String agentId, String userId) {

        // agent如果是自己的则不允许删除
        AgentEntity agent = agentMapper.selectById(agentId);
        if (agent.getUserId().equals(userId)) throw new BusinessException("该助理属于自己，不允许删除");
        boolean deleteAgent = lambdaUpdate().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).remove();
        if (!deleteAgent) throw new BusinessException("删除助理失败");

        List<String> sessionIds = sessionService.getSessionsByAgentId(agentId, userId).stream()
                .map(SessionEntity::getId).collect(Collectors.toList());
        if (sessionIds.isEmpty()) return;

        sessionService.deleteSessions(sessionIds);
        messageService.deleteMessages(sessionIds);

    }

    @Override
    public AgentWorkspaceEntity getWorkspace(String agentId, String userId) {

        AgentWorkspaceEntity agentWorkspaceEntity = lambdaQuery()
                .eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).one();
        if (agentWorkspaceEntity == null) throw new BusinessException("助理不存在");

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
