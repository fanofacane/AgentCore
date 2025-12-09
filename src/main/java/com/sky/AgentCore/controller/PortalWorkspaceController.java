package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Agent工作区 */
@RestController
@RequestMapping("/agents/workspaces")
public class PortalWorkspaceController {
    @Autowired
    private AgentWorkspaceService agentWorkspaceAppService;
    /** 获取工作区下的助理
     *
     * @return */
    @GetMapping("/agents")
    public Result<List<AgentDTO>> getAgents() {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentWorkspaceAppService.getAgents(userId));
    }
}
