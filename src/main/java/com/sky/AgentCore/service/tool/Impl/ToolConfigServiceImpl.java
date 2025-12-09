package com.sky.AgentCore.service.tool.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.tool.ToolConfig;
import com.sky.AgentCore.mapper.ToolConfigMapper;
import com.sky.AgentCore.service.tool.ToolConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ToolConfigServiceImpl extends ServiceImpl<ToolConfigMapper,ToolConfig> implements ToolConfigService {
    @Resource
    private ToolConfigMapper toolConfigMapper;
    @Override
    public List<ToolConfig> findByToolIdInAndIsEnabledTrue(List<String> toolIds) {
        return lambdaQuery().in(ToolConfig::getToolId,toolIds).eq(ToolConfig::getIsEnabled,true).list();
    }
}
