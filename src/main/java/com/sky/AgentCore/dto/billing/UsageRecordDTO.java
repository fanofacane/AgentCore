package com.sky.AgentCore.dto.billing;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** 使用记录DTO 用户使用记录信息传输对象 */
@Data
@NoArgsConstructor
public class UsageRecordDTO {

    /** 记录ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 关联的商品ID */
    private String productId;

    /** 用量数据 */
    private Map<String, Object> quantityData;

    /** 本次用量产生的费用 */
    private BigDecimal cost;

    /** 原始请求ID */
    private String requestId;

    /** 计费发生时间 */
    private LocalDateTime billedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 业务服务名称 （如：GPT-4 模型调用） */
    private String serviceName;

    /** 服务类型 （如：模型服务） */
    private String serviceType;

    /** 服务描述 */
    private String serviceDescription;

    /** 定价规则说明 （如：输入 ¥0.002/1K tokens，输出 ¥0.006/1K tokens） */
    private String pricingRule;

    /** 关联实体名称 （如：具体的模型名称或Agent名称） */
    private String relatedEntityName;
}
