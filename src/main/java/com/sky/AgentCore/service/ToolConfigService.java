package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.tool.ToolConfig;

import java.util.List;

public interface ToolConfigService extends IService<ToolConfig> {
    List<ToolConfig> findByToolIdInAndIsEnabledTrue(List<String> toolIds);
}
