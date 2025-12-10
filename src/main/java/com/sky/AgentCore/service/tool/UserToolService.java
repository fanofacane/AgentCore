package com.sky.AgentCore.service.tool;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.tool.UserToolEntity;

import java.util.List;

public interface UserToolService extends IService<UserToolEntity> {
    List<UserToolEntity> getInstallTool(List<String> toolIds, String userId);
}
