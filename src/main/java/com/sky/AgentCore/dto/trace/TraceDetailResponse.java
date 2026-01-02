package com.sky.AgentCore.dto.trace;


import lombok.Data;

import java.util.List;

/** 追踪详情响应DTO 包含执行汇总和详细步骤信息 */
@Data
public class TraceDetailResponse {

    /** 执行汇总信息 */
    private AgentExecutionSummaryDTO summary;

    /** 执行详细步骤列表 */
    private List<AgentExecutionDetailDTO> details;

    public TraceDetailResponse() {
    }

    public TraceDetailResponse(AgentExecutionSummaryDTO summary, List<AgentExecutionDetailDTO> details) {
        this.summary = summary;
        this.details = details;
    }
}
