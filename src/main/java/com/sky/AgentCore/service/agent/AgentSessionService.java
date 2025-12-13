package com.sky.AgentCore.service.agent;

import com.sky.AgentCore.dto.session.SessionDTO;

import java.util.List;

public interface AgentSessionService {
    SessionDTO createSession(String userId, String agentId);

    List<SessionDTO> getAgentSessionList(String userId, String agentId);

    void updateSession(String id, String userId, String title);

    void deleteSession(String id, String userId);
}
