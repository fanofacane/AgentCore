package com.sky.AgentCore.service.chat.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.chat.ContextEntity;
import com.sky.AgentCore.mapper.ContextMapper;
import com.sky.AgentCore.service.chat.ContextService;
import org.springframework.stereotype.Service;

@Service
public class ContextServiceImpl extends ServiceImpl<ContextMapper, ContextEntity> implements ContextService {
    @Override
    public ContextEntity findBySessionId(String sessionId) {
        return lambdaQuery().eq(ContextEntity::getSessionId, sessionId).one();
    }
}
