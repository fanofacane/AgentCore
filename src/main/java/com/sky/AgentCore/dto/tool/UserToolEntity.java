package com.sky.AgentCore.dto.tool;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListStringConverter;
import com.sky.AgentCore.converter.ToolDefinitionListConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.util.List;

/** 用户工具关联实体类 */
@Data
@TableName(value = "user_tools", autoResultMap = true)
public class UserToolEntity extends BaseEntity {

    /** 唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户ID */
    @TableField("user_id")
    private String userId;

    /** 工具名称 */
    @TableField("name")
    private String name;

    /** 工具描述 */
    @TableField("description")
    private String description;

    /** 工具图标 */
    @TableField("icon")
    private String icon;

    /** 副标题 */
    @TableField("subtitle")
    private String subtitle;

    /** 工具版本ID */
    @TableField("tool_id")
    private String toolId;

    /** 版本号 */
    @TableField("version")
    private String version;

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
    @TableField("public_state")
    private Boolean publicState;

    /** MCP服务器名称 */
    @TableField("mcp_server_name")
    private String mcpServerName;

    /** 是否为全局工具 */
    @TableField("is_global")
    private Boolean isGlobal;
}
