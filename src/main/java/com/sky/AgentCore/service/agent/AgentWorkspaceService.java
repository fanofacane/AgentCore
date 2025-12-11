package com.sky.AgentCore.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;

import java.util.List;

public interface AgentWorkspaceService extends IService<AgentWorkspaceEntity> {
    List<AgentDTO> getAgents(String userId);

    void updateModelConfig(String agentId, String userId, UpdateModelConfigRequest config);

    void deleteAgent(String id, String userId);

    AgentWorkspaceEntity getWorkspace(String agentId, String userId);

    List<AgentWorkspaceEntity> listAgents(List<String> agentIds, String userId);
}
