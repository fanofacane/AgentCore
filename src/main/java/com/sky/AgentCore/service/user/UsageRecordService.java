package com.sky.AgentCore.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;

public interface UsageRecordService extends IService<UsageRecordEntity> {
    boolean existsByRequestId(String requestId);

    void createUsageRecord(UsageRecordEntity usageRecord);
}
