package com.sky.AgentCore.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.memory.CreateMemoryRequest;
import com.sky.AgentCore.dto.memory.MemoryItemDTO;
import com.sky.AgentCore.dto.memory.QueryMemoryRequest;
import com.sky.AgentCore.service.memory.MemoryAppService;
import com.sky.AgentCore.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 用户记忆管理 */
@RestController
@RequestMapping("/portal/memory")
@Validated
public class PortalMemoryController {
    @Autowired
    private MemoryAppService memoryAppService;

    /** 分页列出当前用户的记忆（可选类型过滤） */
    @GetMapping("/items")
    public Result<Page<MemoryItemDTO>> list(QueryMemoryRequest request) {
        String userId = UserContext.getCurrentUserId();
        Page<MemoryItemDTO> page = memoryAppService.listUserMemories(userId, request);
        return Result.success(page);
    }


    /** 手动新增记忆（立即入库并向量化） */
    @PostMapping("/items")
    public Result<?> create(@RequestBody @Valid CreateMemoryRequest request) {
        String userId = UserContext.getCurrentUserId();
        memoryAppService.createMemory(userId, request);
        return Result.success();
    }

    /** 归档（软删除）记忆 */
    @DeleteMapping("/items/{itemId}")
    public Result<Void> delete(@PathVariable String itemId) {
        String userId = UserContext.getCurrentUserId();
        boolean ok = memoryAppService.deleteMemory(userId, itemId);
        return ok ? Result.success() : Result.notFound("记忆不存在或无权限");
    }
}
