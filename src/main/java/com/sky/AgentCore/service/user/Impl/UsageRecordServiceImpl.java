package com.sky.AgentCore.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.assembler.UsageRecordAssembler;
import com.sky.AgentCore.dto.billing.QueryUsageRecordRequest;
import com.sky.AgentCore.dto.billing.UsageRecordDTO;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;
import com.sky.AgentCore.mapper.UsageRecordMapper;
import com.sky.AgentCore.service.user.UsageRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UsageRecordServiceImpl extends ServiceImpl<UsageRecordMapper, UsageRecordEntity> implements UsageRecordService {
    @Autowired
    private UsageRecordMapper usageRecordMapper;
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

    @Override
    public UsageRecordDTO getUsageRecordById(String recordId) {
        UsageRecordEntity record = lambdaQuery().eq(UsageRecordEntity::getId, recordId).one();
        if (record == null) throw new BusinessException("用量记录不存在");
        return UsageRecordAssembler.toDTO(record);
    }

    @Override
    public IPage<UsageRecordDTO> queryUsageRecords(QueryUsageRecordRequest request) {
        // 构建查询条件
        Page<UsageRecordEntity> page = queryUsageRecord(request);
        return page.convert(UsageRecordAssembler::toDTO);
    }
    /** 统计用户的总消费金额
     * @param userId 用户ID
     * @return 总消费金额 */
    @Override
    public BigDecimal getUserTotalCost(String userId) {
        Page<UsageRecordEntity> entityPage = getUserUsageHistory(userId, 1, Integer.MAX_VALUE);

        return entityPage.getRecords().stream().map(UsageRecordEntity::getCost).reduce(BigDecimal.ZERO,
                BigDecimal::add);
    }

    /** 获取用户的用量历史（分页）
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用量记录分页结果 */
    public Page<UsageRecordEntity> getUserUsageHistory(String userId, int pageNum, int pageSize) {
        if (userId == null || userId.trim().isEmpty()) throw new BusinessException("用户ID不能为空");

        LambdaQueryWrapper<UsageRecordEntity> wrapper = Wrappers.<UsageRecordEntity>lambdaQuery()
                .eq(UsageRecordEntity::getUserId, userId).orderByDesc(UsageRecordEntity::getBilledAt);

        Page<UsageRecordEntity> page = new Page<>(pageNum, pageSize);
        return usageRecordMapper.selectPage(page, wrapper);
    }

    public Page<UsageRecordEntity> queryUsageRecord(QueryUsageRecordRequest request) {
        LambdaQueryWrapper<UsageRecordEntity> wrapper = Wrappers.<UsageRecordEntity>lambdaQuery()
                .eq(StringUtils.isNotBlank(request.getUserId()), UsageRecordEntity::getUserId, request.getUserId())
                .eq(StringUtils.isNotBlank(request.getProductId()), UsageRecordEntity::getProductId,
                        request.getProductId())
                .eq(StringUtils.isNotBlank(request.getRequestId()), UsageRecordEntity::getRequestId,
                        request.getRequestId())
                .ge(request.getStartTime() != null, UsageRecordEntity::getBilledAt, request.getStartTime())
                .le(request.getEndTime() != null, UsageRecordEntity::getBilledAt, request.getEndTime())
                .orderByDesc(UsageRecordEntity::getBilledAt);

        return usageRecordMapper.selectPage(new Page<>(request.getPage(), request.getPageSize()), wrapper);
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
