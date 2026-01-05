package com.sky.AgentCore.dto.account;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** 账户充值请求 */
@Data
public class RechargeRequest {

    /** 充值金额 */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0.01")
    @DecimalMax(value = "100000", message = "单次充值金额不能超过10万元")
    private BigDecimal amount;

    /** 支付平台 */
    @NotBlank(message = "支付平台不能为空")
    private String paymentPlatform;

    /** 支付类型 */
    @NotBlank(message = "支付类型不能为空")
    private String paymentType;

    /** 备注信息 */
    private String remark;

    public RechargeRequest() {
    }
}
