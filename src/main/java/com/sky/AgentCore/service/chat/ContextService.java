package com.sky.AgentCore.service.chat;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.chat.ContextEntity;

public interface ContextService extends IService<ContextEntity> {
    ContextEntity findBySessionId(String sessionId);
}
