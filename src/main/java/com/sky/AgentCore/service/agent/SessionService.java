package com.sky.AgentCore.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.session.SessionEntity;

import java.util.List;

public interface SessionService extends IService<SessionEntity> {
    SessionEntity createSession(String agentId, String userId);

    SessionEntity find(String sessionId, String userId);

    void updateSession(String id, String userId, String title);

    void deleteSession(String sessionId, String userId);

    SessionEntity getSession(String sessionId, String userId);
}
