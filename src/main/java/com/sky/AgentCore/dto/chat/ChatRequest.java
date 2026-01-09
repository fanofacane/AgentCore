package com.sky.AgentCore.dto.chat;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {
    /** 消息内容 */
    private String message;

    /** 会话ID */
    private String sessionId;

    /** 文件ID */
    private List<String> fileUrls = new ArrayList<>();
}
