package com.sky.AgentCore.dto.trace;

import lombok.Data;

/** 执行统计响应DTO */
@Data
public class ExecutionStatisticsResponse {

    /** 总执行次数 */
    private Integer totalExecutions;

    /** 成功执行次数 */
    private Integer successfulExecutions;

    /** 失败执行次数 */
    private Integer failedExecutions;

    /** 成功率 */
    private Double successRate;

    /** 总Token使用量 */
    private Long totalTokens;

    public ExecutionStatisticsResponse() {
    }

    public ExecutionStatisticsResponse(Integer totalExecutions, Integer successfulExecutions, Integer failedExecutions,
                                       Double successRate, Long totalTokens) {
        this.totalExecutions = totalExecutions;
        this.successfulExecutions = successfulExecutions;
        this.failedExecutions = failedExecutions;
        this.successRate = successRate;
        this.totalTokens = totalTokens;
    }
}
