package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;

import java.util.List;

public interface AgentWorkspaceService extends IService<AgentWorkspaceEntity> {
    List<AgentDTO> getAgents(String userId);
}
