package com.sky.AgentCore.service.rule.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.rule.RuleEntity;
import com.sky.AgentCore.mapper.RuleMapper;
import com.sky.AgentCore.service.rule.RuleService;
import org.springframework.stereotype.Service;

@Service
public class RuleServiceImpl extends ServiceImpl<RuleMapper, RuleEntity> implements RuleService {
    @Override
    public RuleEntity getRuleById(String ruleId) {
        if (ruleId == null || ruleId.trim().isEmpty()) return null;
        return getRuleById(ruleId);
    }
}
