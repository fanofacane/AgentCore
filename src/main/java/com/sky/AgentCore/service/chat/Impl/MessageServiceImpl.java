package com.sky.AgentCore.service.chat.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.enums.Role;
import com.sky.AgentCore.mapper.ContextMapper;
import com.sky.AgentCore.mapper.MessageMapper;
import com.sky.AgentCore.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {
    @Autowired
    private ContextMapper contextMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    /** 保存消息并且更新消息到上下文 */
    public void saveMessageAndUpdateContext(List<MessageEntity> messageEntities, ContextEntity contextEntity) {
        if (messageEntities == null || messageEntities.isEmpty()) return;

        for (MessageEntity messageEntity : messageEntities) {
            messageEntity.setId(null);
            messageEntity.setCreatedAt(LocalDateTime.now());
        }
        saveBatch(messageEntities);
        contextEntity.getActiveMessages().addAll(messageEntities.stream().map(MessageEntity::getId).toList());
        contextMapper.insertOrUpdate(contextEntity);
    }

    @Override
    public void updateMessage(MessageEntity message) {
        messageMapper.updateById(message);
    }
    /** 获取会话中的消息列表
     *
     * @param sessionId 会话id
     * @return 消息列表 */
    @Override
    public List<MessageEntity> getConversationMessages(String sessionId) {
        return lambdaQuery().eq(MessageEntity::getSessionId, sessionId)
                .ne(MessageEntity::getRole, Role.SUMMARY).orderByAsc(MessageEntity::getCreatedAt).list();
    }

    @Override
    public void deleteConversationMessages(String sessionId) {
        lambdaUpdate().eq(MessageEntity::getSessionId, sessionId).remove();
    }

    @Override
    public void saveMessage(List<MessageEntity> messageEntities) {
        saveBatch(messageEntities);
    }

    @Override
    public boolean isFirstConversation(String sessionId) {
        return lambdaQuery().eq(MessageEntity::getSessionId, sessionId).count() <= 3;
    }

    @Override
    public void deleteMessages(List<String> sessionIds) {
        boolean remove = lambdaUpdate().in(MessageEntity::getSessionId, sessionIds).remove();
        if (!remove) throw new RuntimeException("删除消息失败");
    }
}
