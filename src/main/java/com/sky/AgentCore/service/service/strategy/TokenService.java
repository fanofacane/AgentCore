package com.sky.AgentCore.service.service.strategy;

import com.sky.AgentCore.config.Factory.TokenOverflowStrategyFactory;
import com.sky.AgentCore.dto.message.TokenMessage;
import com.sky.AgentCore.dto.message.TokenProcessResult;
import com.sky.AgentCore.service.service.TokenOverflowStrategy;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TokenService {
    public TokenProcessResult processMessages(List<TokenMessage> messages, TokenOverflowConfig config) {
        // 创建策略
        TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(config);

        // 执行处理
        return strategy.process(messages, config);
    }
}
