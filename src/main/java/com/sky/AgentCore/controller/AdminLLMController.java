package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ProviderCreateRequest;
import com.sky.AgentCore.dto.model.ProviderDTO;
import com.sky.AgentCore.enums.ModelType;
import com.sky.AgentCore.enums.ProviderProtocol;
import com.sky.AgentCore.service.llm.AdminLLMAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 管理员LLM管理 */
@RestController
@RequestMapping("/admin/llms")
public class AdminLLMController {
    @Autowired
    private AdminLLMAppService adminLLMAppService;
    /** 获取服务商列表
     * @param page 页码（可选，默认1）
     * @param pageSize 每页大小（可选，默认20）
     * @return 服务商列表 */
    @GetMapping("/providers")
    public Result<List<ProviderDTO>> getProviders(@RequestParam(required = false, defaultValue = "1") Integer page,
                                                  @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(adminLLMAppService.getOfficialProviders(userId, page, pageSize));
    }

    /** 获取服务商详情
     * @param providerId 服务商ID
     * @return 服务商详情 */
    @GetMapping("/providers/{providerId}")
    public Result<ProviderDTO> getProviderDetail(@PathVariable String providerId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(adminLLMAppService.getProviderDetail(providerId, userId));
    }

    /** 创建服务商
     * @param request 请求对象 */
    @PostMapping("/providers")
    public Result<ProviderDTO> createProvider(@RequestBody @Validated ProviderCreateRequest request) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(adminLLMAppService.createProvider(request, userId));
    }
    /** 获取支持的协议列表
     * @return 协议列表 */
    @GetMapping("/providers/protocols")
    public Result<List<ProviderProtocol>> getProviderProtocols() {
        return Result.success(adminLLMAppService.getProviderProtocols());
    }

    /** 获取模型列表
     * @param providerId 服务商ID（可选，不传则查询所有）
     * @param modelType 模型类型（可选）
     * @param page 页码（可选，默认1）
     * @param pageSize 每页大小（可选，默认20）
     * @return 模型列表 */
    @GetMapping("/models")
    public Result<List<ModelDTO>> getModels(@RequestParam(required = false) String providerId,
                                            @RequestParam(required = false) String modelType,
                                            @RequestParam(required = false, defaultValue = "1") Integer page,
                                            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        String userId = UserContext.getCurrentUserId();
        ModelType type = modelType != null ? ModelType.fromCode(modelType) : null;
        return Result.success(adminLLMAppService.getOfficialModels(userId, providerId, type, page, pageSize));
    }
    /** 获取模型类型列表
     * @return 模型类型列表 */
    @GetMapping("/models/types")
    public Result<List<ModelType>> getModelTypes() {
        return Result.success(adminLLMAppService.getModelTypes());
    }

}
