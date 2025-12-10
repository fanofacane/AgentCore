package com.sky.AgentCore.service.agent;

import com.sky.AgentCore.dto.agent.SystemPromptGenerateRequest;

public interface SystemPromptGeneratorAppService {
    String generateSystemPrompt(SystemPromptGenerateRequest request, String userId);
}
