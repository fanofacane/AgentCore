package com.sky.AgentCore.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.dto.QueryToolRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.tool.ToolVersionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/** 工具市场 */
@RestController
@RequestMapping("/tools")
public class PortalToolController {
    /** 获取已安装的工具列表
     *
     * @return */
    @GetMapping("/installed")
    public Result<Page<ToolVersionDTO>> getInstalledTools(QueryToolRequest queryToolRequest) {
//        String userId = UserContext.getCurrentUserId();
//        return Result.success(toolAppService.getInstalledTools(userId, queryToolRequest));
        Page<ToolVersionDTO> emptyPage = new Page<>(1, 10, 0);
        emptyPage.setRecords(Collections.emptyList());
        return Result.success(emptyPage);
    }
}
