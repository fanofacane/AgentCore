package com.sky.AgentCore.controller.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.dto.tool.QueryToolRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.tool.CreateToolRequest;
import com.sky.AgentCore.dto.tool.ToolDTO;
import com.sky.AgentCore.dto.tool.ToolVersionDTO;
import com.sky.AgentCore.service.tool.ToolService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/** 工具市场 */
@RestController
@RequestMapping("/tools")
public class PortalToolController {
    @Autowired
    private ToolService toolService;
    /** 上传工具
     * @param request 创建工具请求
     * @return 创建的工具信息 */
    @PostMapping
    public Result<ToolDTO> createTool(@RequestBody @Validated CreateToolRequest request) {
        String userId = UserContext.getCurrentUserId();
//        ToolDTO tool = toolService.uploadTool(request, userId);
        return Result.success();
    }
    /** 获取已安装的工具列表
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
