package com.sky.AgentCore.dto.product;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/** 商品DTO */
@Data
public class ProductDTO {

    /** 商品唯一ID */
    private String id;

    /** 商品名称 */
    private String name;

    /** 商品类型 */
    private String type;

    /** 关联的业务ID */
    private String serviceId;

    /** 关联的规则ID */
    private String ruleId;

    /** 价格配置 */
    private Map<String, Object> pricingConfig;

    /** 状态 1-激活 0-禁用 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 模型名称 (仅MODEL_USAGE类型) */
    private String modelName;

    /** 模型标识符 (仅MODEL_USAGE类型) */
    private String modelId;

    /** 服务商名称 (仅MODEL_USAGE类型) */
    private String providerName;
}
