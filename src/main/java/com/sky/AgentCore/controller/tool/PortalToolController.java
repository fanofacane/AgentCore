package com.sky.AgentCore.controller.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.dto.tool.QueryToolRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.tool.ToolDTO;
import com.sky.AgentCore.service.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/** 工具市场 */
@RestController
@RequestMapping("/tools")
public class PortalToolController {
    @Autowired
    private ToolService toolService;
    /** 获取工具列表
     * @return */
    @GetMapping("/enable")
    public Result<Page<ToolDTO>> getEnableTools(QueryToolRequest queryToolRequest) {
        // 1. 核心：调用Service层执行【分页查询】，返回封装好的分页数据
        Page<ToolDTO> toolPage = toolService.getEnableTools(queryToolRequest);

        // 2. 统一返回成功结果，无需手动构建空分页，Mybatis-Plus会自动处理无数据的情况
        return Result.success(toolPage);
    }
}
