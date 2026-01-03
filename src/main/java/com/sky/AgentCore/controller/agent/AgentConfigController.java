package com.sky.AgentCore.controller.agent;

import com.sky.AgentCore.dto.agent.AgentConfig;
import com.sky.AgentCore.mapper.agent.AgentConfigMapper;
import com.sky.AgentCore.service.agent.AgentConfigService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AgentConfigController {
    @Resource
    private AgentConfigService agentConfigService;
    @Resource
    private AgentConfigMapper agentConfigMapper;
    // 按ID查询智能体
    @GetMapping("/agent/{id}")
    public AgentConfig getAgentById(@PathVariable Long id) {
        // 调用BaseMapper的基础方法
        return agentConfigMapper.selectById(id);
    }

    // 按agentId查询
    @GetMapping("/agent/key/{agentId}")
    public AgentConfig getAgentByKey(@PathVariable String agentId) {
        // 调用Service的封装方法
        return agentConfigService.getByAgentId(agentId);
    }

    // 查询启用的智能体
    @GetMapping("/agent/enabled")
    public List<AgentConfig> listEnabledAgents() {
        return agentConfigService.listEnabledAgents();
    }

    // 查询关联某个工具的智能体
    @GetMapping("/agent/tool/{toolId}")
    public List<AgentConfig> listAgentsByToolId(@PathVariable String toolId) {
        return agentConfigService.listAgentsByToolId(toolId);
    }
}
