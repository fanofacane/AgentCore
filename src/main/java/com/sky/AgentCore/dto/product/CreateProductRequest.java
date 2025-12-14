package com.sky.AgentCore.dto.product;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/** 创建商品请求 */
@Data
public class CreateProductRequest {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品类型不能为空")
    private String type;

    @NotBlank(message = "业务ID不能为空")
    private String serviceId;

    @NotBlank(message = "规则ID不能为空")
    private String ruleId;

    @NotNull(message = "价格配置不能为空")
    private Map<String, Object> pricingConfig;

    private Integer status = 1; // 默认激活
}
