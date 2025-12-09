package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.rule.RuleEntity;

public interface RuleService extends IService<RuleEntity> {
    RuleEntity getRuleById(String ruleId);
}
