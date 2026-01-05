package com.sky.AgentCore.dto.memory;

import lombok.Data;

/** 查询记忆的请求参数（分页 + 过滤） */
@Data
public class QueryMemoryRequest {
    private Integer page = 1;
    private Integer pageSize = 20;
    private String type; // 可选：PROFILE/TASK/FACT/EPISODIC
}
