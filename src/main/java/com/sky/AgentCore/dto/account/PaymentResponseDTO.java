package com.sky.AgentCore.dto.account;


import lombok.Data;

import java.math.BigDecimal;

/** 支付响应DTO */
@Data
public class PaymentResponseDTO {

    /** 订单ID */
    private String orderId;

    /** 订单号 */
    private String orderNo;

    /** 支付URL（网页支付时为跳转链接，二维码支付时为二维码内容） */
    private String paymentUrl;

    /** 支付平台 */
    private String paymentMethod;

    /** 支付类型 */
    private String paymentType;

    /** 支付金额 */
    private BigDecimal amount;

    /** 订单标题 */
    private String title;

    /** 订单状态 */
    private String status;

    public PaymentResponseDTO() {
    }
}
