package com.sky.AgentCore.dto.tool;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ToolVersionDTO {

    private String id;

    private String name;

    private String icon;

    private String subtitle;

    private String description;

    private String userId;

    private String version;

    private String toolId;

    private String uploadType;

    private String uploadUrl;

    private List<ToolDefinition> toolList;

    private List<String> labels;

    private Boolean isOffice;

    private Boolean publicStatus;

    private String changeLog;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String userName;

    private List<ToolVersionDTO> versions;

    private Long installCount;

    private String mcpServerName;

    private Boolean isDelete;
}