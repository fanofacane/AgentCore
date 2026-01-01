package com.sky.AgentCore.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sky.AgentCore.dto.billing.QueryUsageRecordRequest;
import com.sky.AgentCore.dto.billing.UsageRecordDTO;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.user.UsageRecordService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/** 使用记录控制层 提供使用记录查询的API接口 */
@RestController
@RequestMapping("/usage-records")
public class PortalUsageRecordController {
    @Autowired
    private UsageRecordService usageRecordService;
    /** 根据ID获取使用记录
     * @param recordId 记录ID
     * @return 使用记录信息 */
    @GetMapping("/{recordId}")
    public Result<UsageRecordDTO> getUsageRecordById(@PathVariable String recordId) {
        UsageRecordDTO record = usageRecordService.getUsageRecordById(recordId);
        return Result.success(record);
    }
    /** 按条件查询当前用户使用记录
     * @param request 查询参数
     * @return 使用记录分页列表 */
    @GetMapping
    public Result<IPage<UsageRecordDTO>> queryUsageRecords(QueryUsageRecordRequest request) {
        // 前台API只能查询当前用户的记录，防止越权
        String userId = UserContext.getCurrentUserId();
        request.setUserId(userId);
        IPage<UsageRecordDTO> records = usageRecordService.queryUsageRecords(request);
        return Result.success(records);
    }

    /** 获取当前用户的总消费金额
     * @return 总消费金额 */
    @GetMapping("/current/total-cost")
    public Result<BigDecimal> getCurrentUserTotalCost() {
        String userId = UserContext.getCurrentUserId();
        BigDecimal totalCost = usageRecordService.getUserTotalCost(userId);
        return Result.success(totalCost);
    }
}
