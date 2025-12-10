package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.*;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.agent.AgentAppService;
import com.sky.AgentCore.service.agent.SystemPromptGeneratorAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 用户Agent管理 */
@RestController
@RequestMapping("/agents")
public class PortalAgentController {
    @Autowired
    private AgentAppService agentAppService;
    @Autowired
    private SystemPromptGeneratorAppService systemPromptGeneratorAppService;
    /** 创建新Agent */
    @PostMapping
    public Result<AgentDTO> createAgent(@RequestBody @Validated CreateAgentRequest request) {
        String userId = UserContext.getCurrentUserId();
        AgentDTO agent = agentAppService.createAgent(request, userId);
        return Result.success(agent);
    }
    /** 获取用户的Agent列表，支持可选的状态和名称过滤 */
    @GetMapping("/user")
    public Result<List<AgentDTO>> getUserAgents(SearchAgentsRequest searchAgentsRequest) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentAppService.getUserAgents(userId, searchAgentsRequest));
    }

    /** 获取Agent详情 */
    @GetMapping("/{agentId}")
    public Result<AgentDTO> getAgent(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        System.out.println("agentid="+agentId);
        return Result.success(agentAppService.getAgent(agentId, userId));
    }
    /** 更新Agent信息（基本信息和配置合并更新） */
    @PutMapping("/{agentId}")
    public Result<AgentDTO> updateAgent(@PathVariable String agentId,
                                        @RequestBody @Validated UpdateAgentRequest request) {
        String userId = UserContext.getCurrentUserId();
        request.setId(agentId);
        return Result.success(agentAppService.updateAgent(request, userId));
    }

    /** 切换Agent的启用/禁用状态 */
    @PutMapping("/{agentId}/toggle-status")
    public Result<AgentDTO> toggleAgentStatus(@PathVariable String agentId) {
        return Result.success(agentAppService.toggleAgentStatus(agentId));
    }
    /** 删除Agent */
    @DeleteMapping("/{agentId}")
    public Result<Void> deleteAgent(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        agentAppService.deleteAgent(agentId, userId);
        return Result.success(null);
    }
    /* 生成系统提示词*/
    @PostMapping("/generate-system-prompt")
    public Result<String> generateSystemPrompt(@RequestBody @Validated SystemPromptGenerateRequest request) {
        String userId = UserContext.getCurrentUserId();
        String systemPrompt = systemPromptGeneratorAppService.generateSystemPrompt(request, userId);
        return Result.success(systemPrompt);
    }
}
