package com.sky.AgentCore.dto.memory;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** 手动创建记忆请求 */
@Data
public class CreateMemoryRequest {

    @NotBlank
    private String type; // PROFILE/TASK/FACT/EPISODIC

    @NotBlank
    @Size(max = 5000)
    private String text;

    private Float importance; // 0~1

    private List<String> tags;

    private Map<String, Object> data;
}
