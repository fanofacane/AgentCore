package com.sky.AgentCore.dto.agent;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class CreateAgentRequest {

    @NotBlank(message = "助理名称不可为空")
    private String name;
    private String description;
    private String avatar;

    private String systemPrompt;
    private String welcomeMessage;
    private List<String> toolIds;
    private List<String> knowledgeBaseIds;
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;
    private Boolean multiModal;
}
