package com.sky.AgentCore.dto.chat;

import lombok.Data;

/** 聊天响应DTO */
@Data
public class ChatResponse {

    /**
     * 响应内容
     */
    private String content;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 使用的服务商
     */
    private String provider;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 创建时间
     */
    private Long timestamp = System.currentTimeMillis();
}
