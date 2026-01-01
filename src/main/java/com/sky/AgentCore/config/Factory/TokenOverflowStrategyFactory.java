package com.sky.AgentCore.config.Factory;

import com.sky.AgentCore.enums.TokenOverflowStrategyEnum;
import com.sky.AgentCore.service.service.strategy.NoTokenOverflowStrategy;
import com.sky.AgentCore.service.service.strategy.SlidingWindowTokenOverflowStrategy;
import com.sky.AgentCore.service.service.strategy.SummarizeTokenOverflowStrategy;
import com.sky.AgentCore.service.service.strategy.TokenOverflowConfig;
import com.sky.AgentCore.service.service.TokenOverflowStrategy;
import org.springframework.stereotype.Service;

/** Token超限处理策略工厂类 根据策略类型创建对应的策略实例 */
@Service
public class TokenOverflowStrategyFactory {

    /** 根据策略类型创建对应的策略实例
     *
     * @param strategyType 策略类型
     * @param config 策略配置
     * @return 策略实例 */
    public static TokenOverflowStrategy createStrategy(TokenOverflowStrategyEnum strategyType,
                                                       TokenOverflowConfig config) {
        if (strategyType == null) {
            return new NoTokenOverflowStrategy();
        }

        switch (strategyType) {
            case SLIDING_WINDOW :
                return new SlidingWindowTokenOverflowStrategy(config);
            case SUMMARIZE :
                return new SummarizeTokenOverflowStrategy(config);
            case NONE :
            default :
                return new NoTokenOverflowStrategy();
        }
    }

    /** 根据策略名称字符串创建对应的策略实例
     *
     * @param strategyName 策略名称字符串
     * @param config 策略配置
     * @return 策略实例 */
    public static TokenOverflowStrategy createStrategy(String strategyName, TokenOverflowConfig config) {
        TokenOverflowStrategyEnum strategyType = TokenOverflowStrategyEnum.fromString(strategyName);
        return createStrategy(strategyType, config);
    }

    /** 根据配置创建对应的策略实例
     *
     * @param config 策略配置
     * @return 策略实例 */
    public static TokenOverflowStrategy createStrategy(TokenOverflowConfig config) {
        return createStrategy(config.getStrategyType(), config);
    }
}
