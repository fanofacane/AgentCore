package com.sky.AgentCore.service.agent.Impl;

import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.SessionAssembler;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.dto.agent.AgentWorkspaceEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.session.SessionDTO;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.enums.Role;
import com.sky.AgentCore.service.agent.*;
import com.sky.AgentCore.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentSessionServiceImpl implements AgentSessionService {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private AgentAppService agentAppService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AgentWorkspaceService agentWorkspaceService;
    @Autowired
    private AgentVersionService agentVersionService;
    /** 创建会话
     *
     * @param userId 用户id
     * @param agentId 助理id
     * @return 会话 */
    @Override
    public SessionDTO createSession(String userId, String agentId) {
        SessionEntity session = sessionService.createSession(agentId, userId);
        AgentEntity agent = agentAppService.getAgentWithPermissionCheck(agentId, userId);
        String welcomeMessage = agent.getWelcomeMessage();
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setRole(Role.SYSTEM);
        messageEntity.setContent(welcomeMessage);
        messageEntity.setSessionId(session.getId());
        messageService.save(messageEntity);
        return SessionAssembler.toDTO(session);
    }
    /** 获取助理下的会话列表
     *
     * @param userId 用户id
     * @param agentId 助理id
     * @return 会话列表 */
    @Override
    public List<SessionDTO> getAgentSessionList(String userId, String agentId) {
        boolean b1 = agentAppService.lambdaQuery().eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, userId).exists();
        boolean b2 = agentWorkspaceService.lambdaQuery().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).exists();
        if (!b1 && !b2) throw new BusinessException("助理不存在");

        // 获取对应的会话列表
        List<SessionEntity> sessions = sessionService.lambdaQuery()
                .eq(SessionEntity::getAgentId, agentId)
                .eq(SessionEntity::getUserId, userId)
                .orderByDesc(SessionEntity::getCreatedAt).list();
        if (sessions.isEmpty()) {
            // 如果会话列表为空，则新创建一个并且返回
            SessionEntity session = sessionService.createSession(agentId, userId);
            sessions.add(session);
            AgentEntity agent = agentAppService.getAgentWithPermissionCheck(agentId, userId);
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setRole(Role.SYSTEM);
            messageEntity.setContent(agent.getWelcomeMessage());
            messageEntity.setSessionId(session.getId());
            messageService.save(messageEntity);
        }

        AgentEntity agent = agentAppService.getById(agentId);
        Boolean multiModal = agent.getMultiModal();
        if (!agent.getUserId().equals(userId)) {
            AgentVersionEntity latestAgentVersion = agentVersionService.lambdaQuery()
                    .eq(AgentVersionEntity::getAgentId, agentId)
                    .orderByDesc(AgentVersionEntity::getPublishedAt)
                    .last("LIMIT 1").one();
            if (latestAgentVersion == null) throw new BusinessException("该Agent暂无版本记录");
            multiModal = latestAgentVersion.getMultiModal();
        }

        List<SessionDTO> dtOs = SessionAssembler.toDTOs(sessions);
        for (SessionDTO dtO : dtOs) {
            dtO.setMultiModal(multiModal);
        }
        return dtOs;
    }

    @Override
    public void updateSession(String id, String userId, String title) {
        sessionService.updateSession(id, userId, title);
    }

    @Override
    public void deleteSession(String sessionId, String userId) {
        sessionService.deleteSession(sessionId, userId);
        // 删除会话下的消息
        messageService.deleteConversationMessages(sessionId);
        // todo 删除定时任务（包括取消延迟队列中的任务）
        //scheduledTaskExecutionService.deleteTasksBySessionId(id, userId);
    }
}
