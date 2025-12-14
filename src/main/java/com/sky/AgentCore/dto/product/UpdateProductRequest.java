package com.sky.AgentCore.dto.product;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/** 更新商品请求 */
@Data
public class UpdateProductRequest {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotBlank(message = "商品类型不能为空")
    private String type;

    @NotBlank(message = "服务ID不能为空")
    private String serviceId;

    @NotBlank(message = "规则ID不能为空")
    private String ruleId;

    private Map<String, Object> pricingConfig;

    private Integer status;
}
