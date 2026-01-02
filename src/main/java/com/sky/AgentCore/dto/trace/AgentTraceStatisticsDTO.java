package com.sky.AgentCore.dto.trace;


import lombok.Data;

import java.time.LocalDateTime;

/** Agent执行链路统计信息DTO */
@Data
public class AgentTraceStatisticsDTO {

    /** Agent ID */
    private String agentId;

    /** Agent名称 */
    private String agentName;

    /** 总执行次数 */
    private Integer totalExecutions;

    /** 成功执行次数 */
    private Integer successfulExecutions;

    /** 失败执行次数 */
    private Integer failedExecutions;

    /** 成功率 */
    private Double successRate;

    /** 总Token数 */
    private Integer totalTokens;

    /** 总输入Token数 */
    private Integer totalInputTokens;

    /** 总输出Token数 */
    private Integer totalOutputTokens;

    /** 工具调用总次数 */
    private Integer totalToolCalls;

    /** 会话总数 */
    private Integer totalSessions;

    /** 最后执行时间 */
    private LocalDateTime lastExecutionTime;

    /** 最后执行状态 */
    private Boolean lastExecutionSuccess;

    public AgentTraceStatisticsDTO() {
    }
}
