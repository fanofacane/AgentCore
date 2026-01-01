package com.sky.AgentCore.service.rule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.product.RuleEntity;

public interface RuleService extends IService<RuleEntity> {
    RuleEntity getRuleById(String ruleId);
}
