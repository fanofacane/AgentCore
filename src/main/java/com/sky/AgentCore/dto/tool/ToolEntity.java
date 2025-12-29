package com.sky.AgentCore.dto.tool;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.*;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.dto.enums.ToolStatus;
import com.sky.AgentCore.dto.enums.ToolType;
import com.sky.AgentCore.dto.enums.UploadType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** 工具实体类 */
@TableName(value = "tools", autoResultMap = true)
@Data
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

    /** 用户ID */
    @TableField("user_id")
    private String userId;

    /** 标签列表 */
    @TableField(value = "labels", typeHandler = ListStringConverter.class)
    private List<String> labels;

    /** 工具类型：mcp */
    @TableField(value = "tool_type", typeHandler = ToolTypeConverter.class)
    private ToolType toolType = ToolType.MCP;

    /** 上传方式：github, zip */
    @TableField(value = "upload_type", typeHandler = UploadTypeConverter.class)
    private UploadType uploadType = UploadType.GITHUB;

    /** 上传URL */
    @TableField("upload_url")
    private String uploadUrl;

    /** 安装命令 */
    @TableField(value = "install_command", typeHandler = MapConverter.class)
    private Map<String, Object> installCommand;

    /** 工具列表 */
    @TableField(value = "tool_list", typeHandler = ToolDefinitionListConverter.class)
    private List<ToolDefinition> toolList;

    /** 审核状态 */
    @TableField(value = "status", typeHandler = ToolStatusConverter.class)
    private ToolStatus status;

    /** 是否官方工具 */
    @TableField("is_office")
    private Boolean isOffice;

    /** 拒绝原因 */
    @TableField("reject_reason")
    private String rejectReason;

    @TableField(value = "failed_step_status", typeHandler = ToolStatusConverter.class)
    private ToolStatus failedStepStatus;

    @TableField("mcp_server_name")
    private String mcpServerName;

    /** 是否为全局工具 */
    @TableField("is_global")
    private Boolean isGlobal;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<tool>\n");
        sb.append("  <name>").append(this.getName()).append("</name>\n");
        sb.append("  <description>").append(this.getDescription()).append("</description>\n");
        if (this.getToolList() != null && !this.getToolList().isEmpty()) {
            sb.append("  <functions>\n");
            for (ToolDefinition def : this.getToolList()) {
                sb.append(def.toString()); // 调用ToolDefinition的toString
            }
            sb.append("  </functions>\n");
        }
        sb.append("</tool>\n");
        return sb.toString();
    }
}
