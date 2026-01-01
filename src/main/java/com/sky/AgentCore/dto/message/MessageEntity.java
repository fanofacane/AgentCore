package com.sky.AgentCore.dto.message;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListConverter;
import com.sky.AgentCore.converter.MessageTypeConverter;
import com.sky.AgentCore.converter.RoleConverter;
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
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属会话ID */
    private String sessionId;

    /** 消息角色 (user, assistant, system) */
    @TableField(value = "role", typeHandler = RoleConverter.class)
    private Role role;

    /** 消息内容 */
    private String content;

    /** 消息类型 */
    @TableField(value = "message_type", typeHandler = MessageTypeConverter.class)
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
    @TableField(value = "file_urls", typeHandler = ListConverter.class)
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

    public boolean isSummaryMessage() {
        return this.role == Role.SUMMARY;
    }
}
