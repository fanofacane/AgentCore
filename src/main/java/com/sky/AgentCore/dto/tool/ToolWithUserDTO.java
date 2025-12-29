package com.sky.AgentCore.dto.tool;


import com.sky.AgentCore.dto.enums.ToolStatus;
import com.sky.AgentCore.dto.enums.ToolType;
import com.sky.AgentCore.dto.enums.UploadType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** 包含用户信息的工具DTO，用于管理员界面 */
@Data
public class ToolWithUserDTO {

    private String id;
    private String name;
    private String icon;
    private String subtitle;
    private String description;
    private String userId;
    private List<String> labels;
    private ToolType toolType;
    private UploadType uploadType;
    private String uploadUrl;
    private Map<String, Object> installCommand;
    private List<ToolDefinition> toolList;
    private ToolStatus status;
    private Boolean isOffice;
    private Integer installCount;
    private String currentVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String rejectReason;
    private ToolStatus failedStepStatus;
    private String mcpServerName;
    private Boolean isGlobal;

    // 用户信息字段
    private String userNickname;
    private String userEmail;
    private String userAvatarUrl;
}
