package com.sky.AgentCore.service.agent;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.agent.AgentConfig;

import java.util.List;


public interface AgentConfigService extends IService<AgentConfig> {
    // 扩展业务方法
    AgentConfig getByAgentId(String agentId);
    List<AgentConfig> listEnabledAgents();
    List<AgentConfig> listAgentsByToolId(String toolId);
}
