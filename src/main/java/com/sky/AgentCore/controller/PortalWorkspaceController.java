package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.LLMModelConfig;
import com.sky.AgentCore.dto.agent.AgentDTO;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Agent工作区 */
@RestController
@RequestMapping("/agents/workspaces")
public class PortalWorkspaceController {
    @Autowired
    private AgentWorkspaceService agentWorkspaceAppService;
    /** 获取工作区下的助理
     * @return */
    @GetMapping("/agents")
    public Result<List<AgentDTO>> getAgents() {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentWorkspaceAppService.getAgents(userId));
    }
    /** 设置agent的模型配置
     * @param config 模型配置
     * @param agentId agentId
     * @return */
    @PutMapping("/{agentId}/model/config")
    public Result<Void> saveModelConfig(@RequestBody @Validated UpdateModelConfigRequest config,
                                        @PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        agentWorkspaceAppService.updateModelConfig(agentId, userId, config);
        return Result.success();
    }
    /** 删除工作区中的助理
     *
     * @param id 助理id */
    @DeleteMapping("/agents/{id}")
    public Result<Void> deleteAgent(@PathVariable String id) {
        String userId = UserContext.getCurrentUserId();
        //todo
        agentWorkspaceAppService.deleteAgent(id, userId);
        return Result.success();
    }
    /** 根据agentId和userId获取对应的modelId
     * @param agentId agentId
     * @return */
    @GetMapping("/{agentId}/model-config")
    public Result<LLMModelConfig> getConfiguredModelId(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentWorkspaceAppService.getConfiguredModelId(agentId, userId));
    }
    /** 添加助理到工作区
     * @param agentId 助理 id
     * @return */
    @PostMapping("/{agentId}")
    public Result<?> addAgent(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        agentWorkspaceAppService.addAgent(agentId, userId);
        return Result.success();
    }

}
