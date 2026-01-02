package com.sky.AgentCore.dto.trace;


import lombok.Data;

/** 执行统计信息DTO */
@Data
public class ExecutionStatisticsDTO {

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

    /** 平均执行时间(毫秒) */
    private Double averageExecutionTime;

    /** 总工具调用次数 */
    private Integer totalToolCalls;
    public ExecutionStatisticsDTO(Integer totalExecutions, Integer successfulExecutions, Long totalTokens) {
        this.totalExecutions = totalExecutions;
        this.successfulExecutions = successfulExecutions;
        this.failedExecutions = totalExecutions - successfulExecutions;
        this.successRate = totalExecutions > 0 ? (double) successfulExecutions / totalExecutions : 0.0;
        this.totalTokens = totalTokens;
    }
}
