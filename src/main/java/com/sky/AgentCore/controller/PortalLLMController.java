package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.enums.ModelType;
import com.sky.AgentCore.enums.ProviderType;
import com.sky.AgentCore.service.LLMAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 大模型服务商 */
@RestController
@RequestMapping("/llms")
public class PortalLLMController {
    @Autowired
    private LLMAppService llmAppService;
    /** 获取所有激活模型
     * @param modelType 模型类型（可选），不传则查询所有类型
     * @param official 是否只获取官方模型（可选），true-仅官方模型，false或不传-所有模型
     * @return 模型列表 */
    @GetMapping("/models")
    public Result<List<ModelDTO>> getModels(@RequestParam(required = false) String modelType,
                                            @RequestParam(required = false) Boolean official) {
        String userId = UserContext.getCurrentUserId();
        ModelType type = modelType != null ? ModelType.fromCode(modelType) : null;
        ProviderType providerType = (official != null && official) ? ProviderType.OFFICIAL : ProviderType.ALL;
        return Result.success(llmAppService.getActiveModelsByType(providerType, userId, type));
    }
    /** 获取用户默认的模型详情
     *
     * @return */
    @GetMapping("/models/default")
    public Result<ModelDTO> getDefaultModel() {
        String userId = UserContext.getCurrentUserId();
        ModelDTO modelDTO = llmAppService.getDefaultModel(userId);
        return Result.success(modelDTO);
    }
}
