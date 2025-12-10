package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import com.sky.AgentCore.mapper.AgentVersionMapper;
import com.sky.AgentCore.service.agent.AgentVersionService;
import org.springframework.stereotype.Service;

@Service
public class AgentVersionServiceImpl extends ServiceImpl<AgentVersionMapper, AgentVersionEntity> implements AgentVersionService {
}
