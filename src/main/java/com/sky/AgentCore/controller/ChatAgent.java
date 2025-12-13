package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.AgentConfig;
import com.sky.AgentCore.service.agent.AgentConfigService;
import com.sky.AgentCore.service.agent.Impl.AgentBuildConfig;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatAgent {
    @Resource
    private AgentConfigService agentConfigService;
    @Resource
    private AgentBuildConfig agentBuildConfig;
    @GetMapping("/agent/chat")
    private String chatStream(@RequestParam String agentId,@RequestParam String message){
        AgentConfig agentConfig = agentBuildConfig.buildAgent(agentId);
        return agentBuildConfig.buildConfiguredChatModel(agentConfig,message);
    }
}
