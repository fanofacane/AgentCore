package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.converter.AgentAssembler;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.mapper.AgentWorkspaceMapper;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AgentWorkspaceServiceImpl extends ServiceImpl<AgentWorkspaceMapper, AgentWorkspaceEntity> implements AgentWorkspaceService {
    @Autowired
    private AgentMapper agentMapper;
    @Override
    public List<AgentDTO> getAgents(String userId) {
        List<AgentWorkspaceEntity> list = lambdaQuery()
                .eq(AgentWorkspaceEntity::getUserId, userId)
                .select(AgentWorkspaceEntity::getAgentId).list();
        List<String> agentIds = list.stream()
                .map(AgentWorkspaceEntity::getAgentId).toList();

        if (agentIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AgentEntity> agentEntities = agentMapper.selectByIds(agentIds);
        System.out.println("agentEntities"+agentEntities);
        return AgentAssembler.toDTOs(agentEntities);
    }
}
