package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.CreateAgentRequest;
import com.sky.AgentCore.dto.agent.SearchAgentsRequest;

import java.util.List;

public interface AgentAppService extends IService<AgentEntity> {
    AgentDTO createAgent(CreateAgentRequest request, String userId);

    List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest);

    AgentDTO getAgent(String agentId, String userId);
}
