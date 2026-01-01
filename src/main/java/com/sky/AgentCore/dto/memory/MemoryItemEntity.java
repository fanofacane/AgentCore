package com.sky.AgentCore.dto.memory;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListStringConverter;
import com.sky.AgentCore.converter.MapConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** 记忆条目实体（memory_items） */
@Data
@TableName("memory_items")
public class MemoryItemEntity extends BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("user_id")
    private String userId;

    @TableField("type")
    private String type; // 使用字符串存储，取值见 MemoryType

    @TableField("text")
    private String text;

    @TableField(value = "data", typeHandler = MapConverter.class)
    private Map<String, Object> data;

    @TableField("importance")
    private Float importance;

    @TableField(value = "tags", typeHandler = ListStringConverter.class)
    private List<String> tags;

    @TableField("source_session_id")
    private String sourceSessionId;

    @TableField("dedupe_hash")
    private String dedupeHash;

    @TableField("status")
    private Integer status; // 1=active, 0=archived/deleted
}
