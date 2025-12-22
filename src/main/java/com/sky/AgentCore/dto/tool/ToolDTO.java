package com.sky.AgentCore.dto.tool;

import com.sky.AgentCore.enums.ToolStatus;
import com.sky.AgentCore.enums.ToolType;
import com.sky.AgentCore.enums.UploadType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 工具DTO */
@Data
public class ToolDTO {
    private String id;
    private String name;
    private String icon;
    private String subtitle;
    private String description;
    private String userId;
    private String userName; // 作者名称
    private List<String> labels;
    private ToolType toolType;
    private UploadType uploadType;
    private String uploadUrl;
    private List<ToolDefinition> toolList;
    private ToolStatus status; // todo 后续可能删除
    private Boolean isOffice;
    private Integer installCount; // 安装数量
    private String currentVersion; // 当前版本号
    private String installCommand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String rejectReason;
    private ToolStatus failedStepStatus;
    private String mcpServerName;
    private Boolean isGlobal;
}
