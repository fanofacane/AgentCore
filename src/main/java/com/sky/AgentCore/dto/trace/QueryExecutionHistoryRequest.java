package com.sky.AgentCore.dto.trace;


import com.sky.AgentCore.dto.tool.Page;
import lombok.Data;

import java.time.LocalDateTime;

/** 查询执行历史请求DTO */
@Data
public class QueryExecutionHistoryRequest extends Page {

    /** 会话ID */
    private String sessionId;

    /** Agent ID */
    private String agentId;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 执行状态：true-成功，false-失败，null-全部 */
    private Boolean executionSuccess;

    /** 关键词搜索 */
    private String keyword;
}
