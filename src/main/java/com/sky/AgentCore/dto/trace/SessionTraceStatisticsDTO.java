package com.sky.AgentCore.dto.trace;


import lombok.Data;

import java.time.LocalDateTime;

/** 会话执行链路统计信息DTO */
@Data
public class SessionTraceStatisticsDTO {

    /** 会话ID */
    private String sessionId;

    /** 会话名称/标题 */
    private String sessionTitle;

    /** Agent ID */
    private String agentId;

    /** Agent名称 */
    private String agentName;

    /** 总执行次数（消息数） */
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

    /** 总执行时间(毫秒) */
    private Integer totalExecutionTime;

    /** 会话创建时间 */
    private LocalDateTime sessionCreatedTime;

    /** 最后执行时间 */
    private LocalDateTime lastExecutionTime;

    /** 最后执行状态 */
    private Boolean lastExecutionSuccess;

    /** 是否已归档 */
    private Boolean isArchived;

    public SessionTraceStatisticsDTO() {
    }

}
