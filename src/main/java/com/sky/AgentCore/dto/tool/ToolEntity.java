package com.sky.AgentCore.dto.tool;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.*;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.ToolStatus;
import com.sky.AgentCore.enums.ToolType;
import com.sky.AgentCore.enums.UploadType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** 工具实体类 */
@Data
@TableName(value = "tools", autoResultMap = true)
public class ToolEntity extends BaseEntity {

    /** 工具唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 工具描述名称 */
    @TableField("name")
    private String name;

    /** 工具图标 */
    @TableField("icon")
    private String icon;

    /** 副标题 */
    @TableField("subtitle")
    private String subtitle;

    /** 工具描述 */
    @TableField("description")
    private String description;

    /** 启用状态 */
    @TableField("status")
    private Boolean status;

    /** 标签列表 */
    @TableField(value = "labels", typeHandler = ListStringConverter.class)
    private List<String> labels;

    /** 工具类型：mcp */
    @TableField(value = "tool_type", typeHandler = ToolTypeConverter.class)
    private ToolType toolType = ToolType.MCP;

    /** 容器URL */
    @TableField("upload_url")
    private String uploadUrl;

    /** 是否官方工具 */
    @TableField("is_office")
    private Boolean isOffice;

    @TableField("mcp_server_name")
    private String mcpServerName;


}
