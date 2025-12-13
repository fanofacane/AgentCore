package com.sky.AgentCore.service.user.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;
import com.sky.AgentCore.mapper.UsageRecordMapper;
import com.sky.AgentCore.service.user.UsageRecordService;
import org.springframework.stereotype.Service;

@Service
public class UsageRecordServiceImpl extends ServiceImpl<UsageRecordMapper, UsageRecordEntity> implements UsageRecordService {
    /** 检查请求ID是否存在（用于幂等性检查）
     * @param requestId 请求ID
     * @return 是否存在 */
    @Override
    public boolean existsByRequestId(String requestId) {
        return checkDuplicateRequest(requestId);
    }

    /** 创建用量记录
     * @param record 用量记录实体
     * @return 保存后的用量记录实体 */
    @Override
    public void createUsageRecord(UsageRecordEntity record) {
        recordUsage(record);
    }
    /** 记录用量
     * @param record 用量记录实体
     * @return 保存后的用量记录实体 */
    public void recordUsage(UsageRecordEntity record) {
        // 验证记录信息
        record.validate();

        // 检查幂等性
        if (checkDuplicateRequest(record.getRequestId())) {
            throw new BusinessException("重复的请求ID: " + record.getRequestId());
        }
        save(record);
    }

    /** 检查请求是否重复（幂等性检查）
     * @param requestId 请求ID
     * @return 是否重复 */
    public boolean checkDuplicateRequest(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) return false;
        return lambdaQuery().eq(UsageRecordEntity::getRequestId, requestId).exists();
    }
}
