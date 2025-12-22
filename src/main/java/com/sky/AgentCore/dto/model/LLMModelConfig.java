package com.sky.AgentCore.dto.model;

import com.sky.AgentCore.enums.TokenOverflowStrategyEnum;
import lombok.Data;
/** Agent模型配置类，用于表示大语言模型的相关配置参数 */
@Data
public class LLMModelConfig {
    /** 模型id */
    private String modelId;

    /** 温度参数，范围0-2，值越大创造性越强，越小则越保守 */
    private Double temperature = 0.7;

    /** Top P参数，范围0-1，控制输出的多样性 */
    private Double topP = 0.7;

    private Integer topK = 50;

    /** 最大Token数，适用于滑动窗口和摘要策略 */
    private Integer maxTokens;
    /** 策略类型 @link TokenOverflowStrategyEnum */
    private TokenOverflowStrategyEnum strategyType = TokenOverflowStrategyEnum.NONE;
    /** 预留缓冲比例，适用于滑动窗口策略 范围0-1之间的小数，表示预留的空间比例 */
    private Double reserveRatio;
    /** 摘要触发阈值（消息数量），适用于摘要策略 */
    private Integer summaryThreshold;
}
