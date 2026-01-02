package com.sky.AgentCore.dto.trace;


import lombok.Data;

import java.time.LocalDateTime;

/** Agent执行链路汇总DTO */
@Data
public class AgentExecutionSummaryDTO {

    /** 追踪ID */
    private String traceId;

    /** 用户ID */
    private String userId;

    /** 会话ID */
    private String sessionId;

    /** Agent ID */
    private String agentId;

    /** Agent名称 */
    private String agentName;

    /** 执行开始时间 */
    private LocalDateTime executionStartTime;

    /** 执行结束时间 */
    private LocalDateTime executionEndTime;

    /** 总执行时间(毫秒) */
    private Integer totalExecutionTime;

    /** 总输入Token数 */
    private Integer totalInputTokens;

    /** 总输出Token数 */
    private Integer totalOutputTokens;

    /** 总Token数 */
    private Integer totalTokens;

    /** 工具调用总次数 */
    private Integer toolCallCount;

    /** 工具执行总耗时(毫秒) */
    private Integer totalToolExecutionTime;

    /** 执行是否成功 */
    private Boolean executionSuccess;

    /** 错误发生阶段 */
    private String errorPhase;

    /** 错误信息 */
    private String errorMessage;

    /** 创建时间 */
    private LocalDateTime createdTime;

    public AgentExecutionSummaryDTO() {
    }
}
