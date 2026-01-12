package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.AgentAssembler;
import com.sky.AgentCore.converter.assembler.AgentVersionAssembler;
import com.sky.AgentCore.converter.assembler.AgentWorkspaceAssembler;
import com.sky.AgentCore.dto.agent.*;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.memory.AgentExecutionDetailEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.enums.PublishStatus;
import com.sky.AgentCore.mapper.agent.AgentExecutionDetailMapper;
import com.sky.AgentCore.mapper.agent.AgentExecutionSummaryMapper;
import com.sky.AgentCore.mapper.agent.ContextMapper;
import com.sky.AgentCore.mapper.agent.AgentMapper;
import com.sky.AgentCore.mapper.agent.AgentVersionMapper;
import com.sky.AgentCore.mapper.agent.AgentWorkspaceMapper;
import com.sky.AgentCore.mapper.session.SessionMapper;
import com.sky.AgentCore.dto.trace.AgentExecutionSummaryEntity;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.agent.SessionService;
import com.sky.AgentCore.service.chat.MessageService;
import com.sky.AgentCore.service.llm.LLMAppService;
import com.sky.AgentCore.service.task.ScheduledTaskExecutionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
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
    @Autowired
    private AgentExecutionDetailMapper detailMapper;
    @Autowired
    private AgentExecutionSummaryMapper summaryMapper;
    @Autowired
    private ContextMapper contextMapper;
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private ScheduledTaskExecutionService scheduledTaskExecutionService;
    @Override
    public List<AgentDTO> getAgents(String userId) {
        //获取用户添加到工作区的agent
        List<AgentWorkspaceEntity> list = lambdaQuery()
                .eq(AgentWorkspaceEntity::getUserId, userId)
                .select(AgentWorkspaceEntity::getAgentId).list();
        if (list.isEmpty()) return Collections.emptyList();

        //收集ids
        List<String> agentIds = list.stream()
                .map(AgentWorkspaceEntity::getAgentId).toList();

        LambdaQueryWrapper<AgentEntity> wrap = new LambdaQueryWrapper<>();
        wrap.in(AgentEntity::getId, agentIds);
        List<AgentEntity> agentEntities = agentMapper.selectList(wrap);
        Map<String, AgentEntity> agentEntityMap = new HashMap<>();
        Map<String, Boolean> agentEnabledMap;
        if (!agentEntities.isEmpty()) {
            agentEntityMap = agentEntities.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(AgentEntity::getId, a -> a, (a, b) -> a));
            agentEnabledMap = agentEntityMap.values().stream()
                    .collect(Collectors.toMap(AgentEntity::getId, AgentEntity::getEnabled, (a, b) -> a));
        } else {
            agentEnabledMap = new HashMap<>();
        }

        Set<String> agentIdSet = new HashSet<>(agentIds);
        if (agentIdSet.isEmpty()) return Collections.emptyList();

        Map<String, AgentVersionEntity> bestVersionMap = new HashMap<>();
        List<AgentVersionEntity> latestPublished = agentVersionMapper.selectLatestVersionsByAgentIdsAndStatus(
                new ArrayList<>(agentIdSet),
                PublishStatus.PUBLISHED.getCode()
        );
        if (latestPublished != null && !latestPublished.isEmpty()) {
            for (AgentVersionEntity v : latestPublished) {
                if (v == null || !StringUtils.hasText(v.getAgentId())) continue;
                bestVersionMap.merge(v.getAgentId(), v, (existing, incoming) -> {
                    LocalDateTime existingPublishedAt = existing.getPublishedAt();
                    LocalDateTime incomingPublishedAt = incoming.getPublishedAt();
                    if (existingPublishedAt != null && incomingPublishedAt != null) {
                        int compare = incomingPublishedAt.compareTo(existingPublishedAt);
                        if (compare != 0) return compare > 0 ? incoming : existing;
                    } else if (incomingPublishedAt != null) {
                        return incoming;
                    } else if (existingPublishedAt != null) {
                        return existing;
                    }

                    LocalDateTime existingCreatedAt = existing.getCreatedAt();
                    LocalDateTime incomingCreatedAt = incoming.getCreatedAt();
                    if (existingCreatedAt != null && incomingCreatedAt != null) {
                        int compare = incomingCreatedAt.compareTo(existingCreatedAt);
                        if (compare != 0) return compare > 0 ? incoming : existing;
                    } else if (incomingCreatedAt != null) {
                        return incoming;
                    } else if (existingCreatedAt != null) {
                        return existing;
                    }

                    String existingId = existing.getId();
                    String incomingId = incoming.getId();
                    if (existingId == null) return incoming;
                    if (incomingId == null) return existing;
                    return incomingId.compareTo(existingId) > 0 ? incoming : existing;
                });
            }
        }

        Set<String> missingAgentIds = new HashSet<>(agentIdSet);
        missingAgentIds.removeAll(bestVersionMap.keySet());
        if (!missingAgentIds.isEmpty()) {
            List<AgentVersionEntity> latestRemoved = agentVersionMapper.selectLatestVersionsByAgentIdsAndStatus(
                    new ArrayList<>(missingAgentIds),
                    PublishStatus.REMOVED.getCode()
            );
            if (latestRemoved != null && !latestRemoved.isEmpty()) {
                for (AgentVersionEntity v : latestRemoved) {
                    if (v == null || !StringUtils.hasText(v.getAgentId())) continue;
                    bestVersionMap.putIfAbsent(v.getAgentId(), v);
                }
            }
        }

        List<AgentDTO> result = new ArrayList<>();
        for (String agentId : agentIds) {
            AgentVersionEntity bestVersion = bestVersionMap.get(agentId);
            AgentDTO dto = new AgentDTO();
            if (bestVersion != null) {
                BeanUtils.copyProperties(bestVersion, dto);
                dto.setId(bestVersion.getAgentId());
            } else {
                AgentEntity agentEntity = agentEntityMap.get(agentId);
                if (agentEntity == null) {
                    continue;
                }
                BeanUtils.copyProperties(agentEntity, dto);
                dto.setId(agentEntity.getId());
            }
            dto.setEnabled(agentEnabledMap.getOrDefault(agentId, Boolean.FALSE));
            result.add(dto);
        }
        return result;
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
        if (agent == null) throw new BusinessException("助理不存在");
        if (agent.getUserId().equals(userId)) throw new BusinessException("该助理属于自己，不允许删除");
        boolean deleteAgent = lambdaUpdate().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).remove();
        if (!deleteAgent) throw new BusinessException("删除助理失败");

        scheduledTaskExecutionService.deleteTasksByAgentId(agentId, userId);

        List<String> sessionIds = sessionService.getSessionsByAgentId(agentId, userId).stream()
                .map(SessionEntity::getId).collect(Collectors.toList());
        if (sessionIds.isEmpty()) return;

        int batchSize = 500;
        for (int i = 0; i < sessionIds.size(); i += batchSize) {
            List<String> batchSessionIds = sessionIds.subList(i, Math.min(i + batchSize, sessionIds.size()));
            detailMapper.delete(new LambdaQueryWrapper<AgentExecutionDetailEntity>()
                    .in(AgentExecutionDetailEntity::getSessionId, batchSessionIds));
            summaryMapper.delete(new LambdaQueryWrapper<AgentExecutionSummaryEntity>()
                    .in(AgentExecutionSummaryEntity::getSessionId, batchSessionIds));
            contextMapper.delete(new LambdaQueryWrapper<ContextEntity>()
                    .in(ContextEntity::getSessionId, batchSessionIds));
            messageService.remove(new LambdaQueryWrapper<MessageEntity>()
                    .in(MessageEntity::getSessionId, batchSessionIds));
            sessionMapper.delete(new LambdaQueryWrapper<SessionEntity>()
                    .in(SessionEntity::getId, batchSessionIds)
                    .eq(SessionEntity::getUserId, userId));
        }

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
        if (agent == null) {
            throw new BusinessException("助理不存在");
        }
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
