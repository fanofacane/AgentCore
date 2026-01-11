package com.sky.AgentCore.service.tool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.tool.QueryToolRequest;
import com.sky.AgentCore.dto.tool.ToolDTO;
import com.sky.AgentCore.dto.tool.ToolEntity;

import java.util.List;

public interface ToolService extends IService<ToolEntity> {

    Page<ToolDTO> getEnableTools(QueryToolRequest queryToolRequest);

    List<ToolEntity> getAllEnableTools(List<String> toolIds);
}
