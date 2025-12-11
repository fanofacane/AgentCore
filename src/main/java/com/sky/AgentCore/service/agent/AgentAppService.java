package com.sky.AgentCore.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.*;

import java.util.List;

public interface AgentAppService extends IService<AgentEntity> {
    AgentDTO createAgent(CreateAgentRequest request, String userId);

    List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest);

    AgentDTO getAgent(String agentId, String userId);

    AgentDTO updateAgent(UpdateAgentRequest request, String userId);

    AgentDTO toggleAgentStatus(String agentId);

    void deleteAgent(String agentId, String userId);

    AgentEntity getAgentWithPermissionCheck(String agentId, String userId);

    AgentEntity getAgentById(String agentId);

    AgentVersionEntity getLatestAgentVersion(String agentId);

    List<AgentVersionDTO> getPublishedAgentsByName(SearchAgentsRequest searchAgentsRequest, String userId);

    AgentVersionDTO publishAgentVersion(String agentId, PublishAgentVersionRequest request, String userId);

    AgentVersionDTO getAgentVersion(String agentId, String versionNumber);

    List<AgentVersionDTO> getAgentVersions(String agentId, String userId);
}
