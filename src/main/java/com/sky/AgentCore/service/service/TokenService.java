package com.sky.AgentCore.service.service;

import com.sky.AgentCore.config.TokenOverflowStrategyFactory;
import com.sky.AgentCore.dto.message.TokenMessage;
import com.sky.AgentCore.dto.message.TokenProcessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TokenService {
    @Autowired
    private TokenOverflowStrategyFactory strategyFactory;
    public TokenProcessResult processMessages(List<TokenMessage> messages, TokenOverflowConfig config) {
        // 创建策略
        TokenOverflowStrategy strategy = strategyFactory.createStrategy(config);

        // 执行处理
        return strategy.process(messages, config);
    }
}
