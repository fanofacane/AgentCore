package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.agent.AgentConfig;
import com.sky.AgentCore.mapper.AgentConfigMapper;
import com.sky.AgentCore.service.agent.AgentConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgentConfigServiceImpl extends ServiceImpl<AgentConfigMapper, AgentConfig> implements AgentConfigService {

    @Resource
    private AgentConfigMapper agentConfigMapper;

    @Override
    public AgentConfig getByAgentId(String agentId) {
        // 调用Mapper的自定义方法
        return lambdaQuery().eq(AgentConfig::getAgentId,agentId).one();
    }

    @Override
    public List<AgentConfig> listEnabledAgents() {
        // 调用Mapper的默认方法
        return lambdaQuery().eq(AgentConfig::getIsEnabled,true).list();
    }

    @Override
    public List<AgentConfig> listAgentsByToolId(String toolId) {
        return lambdaQuery().eq(AgentConfig::getToolIds,toolId).list();
    }

    // 批量新增（MyBatis-Plus Service自带）
    // boolean saveBatch(List<AgentConfig> agentConfigList);
}
