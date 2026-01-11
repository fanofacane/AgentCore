package com.sky.AgentCore.dto.tool;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class ToolDTO {

    private String id;

    private String name;

    private String icon;

    private String subtitle;

    private String description;

    private String uploadType;

    private List<String> labels;

    private Boolean isOffice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String mcpServerName;

    private Boolean isDelete;
}