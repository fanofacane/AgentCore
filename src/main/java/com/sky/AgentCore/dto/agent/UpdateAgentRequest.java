package com.sky.AgentCore.dto.agent;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class UpdateAgentRequest {
    private String id;
    @NotBlank(message = "助理名称不可为空")
    private String name;
    private String avatar;
    private String description;
    private Boolean enabled;

    // 配置信息字段
    private String systemPrompt;
    private String welcomeMessage;
    private LLMModelConfig modelConfig;
    private List<String> toolIds;
    private List<String> knowledgeBaseIds;
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;
    private Boolean multiModal;
}
