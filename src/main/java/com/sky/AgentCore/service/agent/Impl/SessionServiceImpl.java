package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.mapper.session.MessageMapper;
import com.sky.AgentCore.mapper.session.SessionMapper;
import com.sky.AgentCore.service.agent.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl extends ServiceImpl<SessionMapper, SessionEntity> implements SessionService {
    @Autowired
    private MessageMapper messageMapper;
    /** 创建会话
     * @param agentId 助理id
     * @param userId 用户id */
    @Override
    public SessionEntity createSession(String agentId, String userId) {
        SessionEntity session = new SessionEntity();
        session.setAgentId(agentId);
        session.setUserId(userId);
        session.setTitle("新会话");
        save(session);
        return session;
    }

    @Override
    public SessionEntity find(String sessionId, String userId) {
        SessionEntity session = lambdaQuery().eq(SessionEntity::getId, sessionId).eq(SessionEntity::getUserId, userId).one();
        if (session == null) throw new RuntimeException("会话不存在");
        return session;
    }

    @Override
    public void updateSession(String id, String userId, String title) {
        SessionEntity session = new SessionEntity();
        session.setTitle(title);
        session.setId(id);
        session.setUserId(userId);
        lambdaUpdate().eq(SessionEntity::getId, id).eq(SessionEntity::getUserId, userId)
                .update(session);
    }

    @Override
    public void deleteSession(String sessionId, String userId) {
        this.lambdaUpdate().eq(SessionEntity::getId, sessionId)
                .eq(SessionEntity::getUserId, userId).remove();
    }

    @Override
    public SessionEntity getSession(String sessionId, String userId) {
        SessionEntity session = lambdaQuery()
                .eq(SessionEntity::getId, sessionId)
                .eq(SessionEntity::getUserId, userId).one();
        if (session == null) throw new RuntimeException("会话不存在");
        return session;
    }

    @Override
    public List<SessionEntity> getSessionsByAgentId(String agentId, String userId) {
        return lambdaQuery().eq(SessionEntity::getAgentId, agentId)
                .eq(SessionEntity::getUserId, userId).orderByDesc(SessionEntity::getCreatedAt).list();
    }

    @Override
    public void deleteSessions(List<String> sessionIds) {
        boolean remove = lambdaUpdate().in(SessionEntity::getId, sessionIds).remove();
        if (!remove) throw new RuntimeException("删除会话失败");
    }


}
