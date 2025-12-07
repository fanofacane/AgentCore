package com.sky.AgentCore.dto.message;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.MessageType;
import com.sky.AgentCore.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 消息实体类，代表对话中的一条消息 */
@Data
@TableName("messages")
public class MessageEntity extends BaseEntity {

    /** 消息唯一ID */
    private String id;

    /** 所属会话ID */
    private String sessionId;

    /** 消息角色 (user, assistant, system) */
    private Role role;

    /** 消息内容 */
    private String content;

    /** 消息类型 */
    private MessageType messageType = MessageType.TEXT;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** Token数量 */
    private Integer tokenCount = 0;

    /** 消息本体Token数量 */
    private Integer bodyTokenCount = 0;

    /** 服务提供商 */
    private String provider;

    /** 使用的模型 */
    private String model;

    /** 消息元数据 */
    private String metadata;

    private List<String> fileUrls = new ArrayList<>();
    public boolean isUserMessage() {
        return this.role == Role.USER;
    }

    public boolean isAIMessage() {
        return this.role == Role.ASSISTANT;
    }

    public boolean isSystemMessage() {
        return this.role == Role.SYSTEM;
    }
}
