package com.sky.AgentCore.dto.trace;


import com.sky.AgentCore.dto.tool.Page;
import lombok.Data;

import java.time.LocalDateTime;

/** 会话执行链路列表查询请求DTO */
@Data
public class SessionTraceListRequest extends Page {

    /** 关键词搜索（会话标题模糊匹配） */
    private String keyword;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 执行状态筛选：true-有成功执行，false-仅失败，null-全部 */
    private Boolean hasSuccessfulExecution;

    /** 是否包含已归档会话 */
    private Boolean includeArchived;
}
