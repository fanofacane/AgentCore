package com.sky.AgentCore.dto.message;



import com.sky.AgentCore.dto.enums.MessageType;
import com.sky.AgentCore.dto.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 消息DTO，用于API响应 */
@Data
public class MessageDTO {
    /** 消息ID */
    private String id;
    /** 消息角色 */
    private Role role;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 提供商 */
    private String provider;
    /** 模型 */
    private String model;

    /** 消息类型 */
    private MessageType messageType;

    private List<String> fileUrls = new ArrayList<>();
}