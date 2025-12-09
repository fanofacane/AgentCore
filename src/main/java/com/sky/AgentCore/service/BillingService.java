package com.sky.AgentCore.service;

import com.sky.AgentCore.dto.billing.RuleContext;

public interface BillingService {
    boolean checkBalance(RuleContext billingContext);

    void charge(RuleContext billingContext);
}
