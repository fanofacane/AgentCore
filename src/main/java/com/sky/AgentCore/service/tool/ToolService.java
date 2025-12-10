package com.sky.AgentCore.service.tool;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.tool.ToolEntity;

import java.util.List;

public interface ToolService extends IService<ToolEntity> {
    List<ToolEntity> getByIds(List<String> toolIds);
}
