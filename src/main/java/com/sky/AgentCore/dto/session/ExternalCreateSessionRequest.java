package com.sky.AgentCore.dto.session;

import lombok.Data;

/** 外部API创建会话请求DTO */

@Data
public class ExternalCreateSessionRequest {

    /** 会话标题（可选，默认"新会话"） */
    private String title;
}