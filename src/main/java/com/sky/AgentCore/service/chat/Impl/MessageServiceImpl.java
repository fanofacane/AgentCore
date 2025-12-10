package com.sky.AgentCore.service.chat.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.enums.Role;
import com.sky.AgentCore.mapper.MessageMapper;
import com.sky.AgentCore.service.chat.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {
    @Override
    /** 保存消息并且更新消息到上下文 */
    public void saveMessageAndUpdateContext(List<MessageEntity> messageEntities, ContextEntity contextEntity) {
        if (messageEntities == null || messageEntities.isEmpty()) {
            return;
        }
        for (MessageEntity messageEntity : messageEntities) {
            messageEntity.setId(null);
            messageEntity.setCreatedAt(LocalDateTime.now());
        }
        //messageRepository.insert(messageEntities);
        contextEntity.getActiveMessages().addAll(messageEntities.stream().map(MessageEntity::getId).toList());
        //contextRepository.insertOrUpdate(contextEntity);
    }

    @Override
    public void updateMessage(MessageEntity message) {
        //messageRepository.updateById(message);
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
}
