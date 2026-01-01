package com.sky.AgentCore.dto.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 上下文实体类，管理会话的上下文窗口 */
@Data
@TableName(value = "context",autoResultMap = true)
public class ContextEntity extends BaseEntity {

    /** 上下文唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 所属会话ID */
    private String sessionId;

    /** 活跃消息ID列表 */
    @TableField(value = "active_messages", typeHandler = ListConverter.class)
    private List<String> activeMessages = new ArrayList<>();

    /** 历史消息摘要 */
    private String summary;
}
