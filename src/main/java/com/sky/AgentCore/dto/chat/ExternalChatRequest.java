package com.sky.AgentCore.dto.chat;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 外部API聊天请求DTO */
@Data
public class ExternalChatRequest {

    /** 模型ID（可选，不传使用Agent绑定的模型） */
    private String model;

    /** 消息内容 */
    @NotBlank(message = "消息内容不可为空")
    private String message;

    /** 是否流式返回（可选，默认false） */
    private Boolean stream = false;

    /** 会话ID */
    private String sessionId;

    /** 文件列表（可选） */
    private List<String> files = new ArrayList<>();

}
