package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.agent.CreateAgentRequest;
import com.sky.AgentCore.dto.agent.SearchAgentsRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.agent.AgentAppService;
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
//    * 生成系统提示词
/*    @PostMapping("/generate-system-prompt")
    public Result<String> generateSystemPrompt(@RequestBody @Validated SystemPromptGenerateRequest request) {
        String userId = UserContext.getCurrentUserId();
        String systemPrompt = systemPromptGeneratorAppService.generateSystemPrompt(request, userId);
        return Result.success(systemPrompt);
    }*/
}
