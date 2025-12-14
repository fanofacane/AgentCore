package com.sky.AgentCore.dto.billing;


import com.sky.AgentCore.dto.Page;
import lombok.Data;

import java.time.LocalDateTime;

/** 查询使用记录请求 */
@Data
public class QueryUsageRecordRequest extends Page {

    /** 用户ID */
    private String userId;

    /** 商品ID */
    private String productId;

    /** 请求ID */
    private String requestId;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    public QueryUsageRecordRequest() {
    }
}
