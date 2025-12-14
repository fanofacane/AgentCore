package com.sky.AgentCore.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.billing.QueryUsageRecordRequest;
import com.sky.AgentCore.dto.billing.UsageRecordDTO;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;

import java.math.BigDecimal;

public interface UsageRecordService extends IService<UsageRecordEntity> {
    boolean existsByRequestId(String requestId);

    void createUsageRecord(UsageRecordEntity usageRecord);

    UsageRecordDTO getUsageRecordById(String recordId);

    IPage<UsageRecordDTO> queryUsageRecords(QueryUsageRecordRequest request);

    BigDecimal getUserTotalCost(String userId);
}
