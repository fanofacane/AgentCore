package com.sky.AgentCore.service;

import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.dto.message.MessageEntity;

import java.util.List;


public interface MessageDomainService {
    void saveMessageAndUpdateContext(List<MessageEntity> messageEntities, ContextEntity contextEntity);

    void updateMessage(MessageEntity userEntity);
}
