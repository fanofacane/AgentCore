package com.sky.AgentCore.dto.trace;


import lombok.Data;

import java.util.List;

/** 完整的执行链路DTO 包含汇总信息和详细步骤信息 */
@Data
public class ExecutionTraceDTO {

    /** 执行汇总信息 */
    private AgentExecutionSummaryDTO summary;

    /** 执行详细步骤列表 */
    private List<AgentExecutionDetailDTO> details;

    /** 用户消息步骤 */
    private List<AgentExecutionDetailDTO> userMessages;

    /** AI响应步骤 */
    private List<AgentExecutionDetailDTO> aiResponses;

    /** 工具调用步骤 */
    private List<AgentExecutionDetailDTO> toolCalls;

    /** 异常消息步骤 */
    private List<AgentExecutionDetailDTO> errorMessages;

    /** 降级调用步骤 */
    private List<AgentExecutionDetailDTO> fallbackCalls;

    /** 失败的步骤 */
    private List<AgentExecutionDetailDTO> failedSteps;

    public ExecutionTraceDTO() {
    }

    public ExecutionTraceDTO(AgentExecutionSummaryDTO summary, List<AgentExecutionDetailDTO> details) {
        this.summary = summary;
        this.details = details;
    }
}
