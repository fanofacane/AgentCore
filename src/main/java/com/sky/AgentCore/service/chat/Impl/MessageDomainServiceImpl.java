package com.sky.AgentCore.service.chat.Impl;

import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.service.chat.MessageDomainService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class MessageDomainServiceImpl implements MessageDomainService {
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
}
