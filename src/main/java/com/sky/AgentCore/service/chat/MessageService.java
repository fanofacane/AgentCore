package com.sky.AgentCore.service.chat;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageEntity;

import java.util.List;


public interface MessageService extends IService<MessageEntity> {
    void saveMessageAndUpdateContext(List<MessageEntity> messageEntities, ContextEntity contextEntity);

    void updateMessage(MessageEntity userEntity);

    List<MessageEntity> getConversationMessages(String sessionId);

    void deleteConversationMessages(String sessionId);

    void saveMessage(List<MessageEntity> messageEntities);

    boolean isFirstConversation(String sessionId);
}
