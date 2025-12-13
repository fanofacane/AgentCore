package com.sky.AgentCore.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;

import java.util.List;

public interface AgentVersionService extends IService<AgentVersionEntity> {
    List<AgentVersionEntity> getPublishedAgentsByName(AgentEntity entity);

    AgentVersionEntity getLatestAgentVersion(String agentId);
}
