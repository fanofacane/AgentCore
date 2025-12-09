package com.sky.AgentCore.dto.agent;

import lombok.Data;

import java.util.List;
@Data
public class SystemPromptGenerateRequest {
    /** Agent名称 */
    private String agentName;

    /** Agent描述 */
    private String agentDescription;

    /** 工具ID列表 */
    private List<String> toolIds;
}
