package com.sky.AgentCore.dto.memory;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 记忆条目 DTO */
@Data
public class MemoryItemDTO {
    private String id;
    private String type;
    private String text;
    private Float importance;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
