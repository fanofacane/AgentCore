package com.sky.AgentCore.dto.account;


import com.sky.AgentCore.enums.OrderStatus;
import com.sky.AgentCore.enums.PaymentPlatform;
import com.sky.AgentCore.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;

/** 订单状态响应DTO */
@Data
public class OrderStatusResponseDTO {

    /** 订单ID */
    private String orderId;

    /** 订单号 */
    private String orderNo;

    /** 订单状态 */
    private OrderStatus status;

    /** 支付平台 */
    private PaymentPlatform paymentPlatform;

    /** 支付类型 */
    private PaymentType paymentType;

    /** 订单金额 */
    private BigDecimal amount;

    /** 订单标题 */
    private String title;

    /** 支付URL（二维码内容或跳转链接） */
    private String paymentUrl;

    /** 创建时间 */
    private String createdAt;

    /** 更新时间 */
    private String updatedAt;

    /** 过期时间 */
    private String expiredAt;

    public OrderStatusResponseDTO() {
    }
}
