package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.dto.enums.PublishStatus;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.mapper.AgentVersionMapper;
import com.sky.AgentCore.service.agent.AgentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AgentVersionServiceImpl extends ServiceImpl<AgentVersionMapper, AgentVersionEntity> implements AgentVersionService {
    @Autowired
    private AgentVersionMapper agentVersionMapper;
    @Autowired
    private AgentMapper agentMapper;
    @Override
    public List<AgentVersionEntity> getPublishedAgentsByName(AgentEntity entity) {
        List<AgentVersionEntity> list = agentVersionMapper
                .selectLatestVersionsByNameAndStatus(entity.getName(), PublishStatus.PUBLISHED.getCode());
        return combineAgentsWithVersions(list);
    }

    @Override
    public AgentVersionEntity getLatestAgentVersion(String agentId) {
        return lambdaQuery().eq(AgentVersionEntity::getAgentId, agentId)
                .orderByDesc(AgentVersionEntity::getPublishedAt)
                .orderByDesc(AgentVersionEntity::getId)
                .last("limit 1")
                .one();
    }

    /** 组合助理和版本信息
     *
     * @param versionEntities 版本实体列表
     * @return 组合后的版本AgentVersionEntity列表 */
    private List<AgentVersionEntity> combineAgentsWithVersions(List<AgentVersionEntity> versionEntities) {
        // 如果版本列表为空，直接返回空列表
        if (versionEntities == null || versionEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 根据版本中的 agent_id 以及 enable == true 查出对应的 agents
        List<AgentEntity> agents = agentMapper.selectList(Wrappers.<AgentEntity>lambdaQuery()
                .in(AgentEntity::getId,
                        versionEntities.stream().map(AgentVersionEntity::getAgentId).collect(Collectors.toList()))
                .eq(AgentEntity::getEnabled, true));

        // 将版本转为 map，key：agent_id，value：本身
        Map<String, AgentVersionEntity> agentVersionMap = versionEntities.stream()
                .collect(Collectors.toMap(AgentVersionEntity::getAgentId, Function.identity()));

        return agents.stream().map(agent -> agentVersionMap.get(agent.getId())).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
