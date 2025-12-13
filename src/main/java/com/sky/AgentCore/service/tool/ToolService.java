package com.sky.AgentCore.service.tool;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.tool.ToolEntity;
import com.sky.AgentCore.dto.tool.UserToolEntity;

import java.util.List;

public interface ToolService extends IService<ToolEntity> {
    List<ToolEntity> getByIds(List<String> toolIds);

    UserToolEntity findByToolIdAndUserId(String toolId, String userId);
}
