package com.sky.AgentCore.dto.tool;

import com.sky.AgentCore.dto.enums.ToolStatus;
import lombok.Data;

/** 工具查询请求 */
@Data
public class QueryToolRequest extends Page {

    /** 搜索关键词（工具名称、描述） */
    private String keyword;

    /** 工具状态筛选 */
    private ToolStatus status;

    /** 是否为官方工具 */
    private Boolean isOffice;

    /** 兼容原有字段 */
    private String toolName;
}
