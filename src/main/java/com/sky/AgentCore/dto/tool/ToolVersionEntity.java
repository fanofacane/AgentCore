package com.sky.AgentCore.dto.tool;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListStringConverter;
import com.sky.AgentCore.converter.ToolDefinitionListConverter;
import com.sky.AgentCore.converter.UploadTypeConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.UploadType;
import lombok.Data;

import java.util.List;

/** 工具版本实体类 */
@Data
@TableName(value = "tool_versions", autoResultMap = true)
public class ToolVersionEntity extends BaseEntity {

    /** 版本唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 工具名称 */
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

    /** 用户ID */
    @TableField("user_id")
    private String userId;

    /** 版本号 */
    @TableField("version")
    private String version;

    /** 工具ID */
    @TableField("tool_id")
    private String toolId;

    /** 上传方式：github, zip */
    @TableField(value = "upload_type", typeHandler = UploadTypeConverter.class)
    private UploadType uploadType;

    /** 上传URL */
    @TableField("upload_url")
    private String uploadUrl;

    /** 工具列表 */
    @TableField(value = "tool_list", typeHandler = ToolDefinitionListConverter.class)
    private List<ToolDefinition> toolList;

    /** 标签列表 */
    @TableField(value = "labels", typeHandler = ListStringConverter.class)
    private List<String> labels;

    /** 是否官方工具 */
    @TableField("is_office")
    private Boolean isOffice;

    /** 公开状态 */
    @TableField("public_status")
    private Boolean publicStatus;

    /** 变更日志 */
    @TableField("change_log")
    private String changeLog;

    /** MCP服务器名称 */
    @TableField("mcp_server_name")
    private String mcpServerName;
}
