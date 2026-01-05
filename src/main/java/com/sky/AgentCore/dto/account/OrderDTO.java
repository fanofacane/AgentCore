package com.sky.AgentCore.dto.account;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
@Data
public class OrderDTO {

    /** 订单ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 用户昵称 */
    private String userNickname;

    /** 订单号 */
    private String orderNo;

    /** 订单类型 */
    private String orderType;

    /** 订单标题 */
    private String title;

    /** 订单描述 */
    private String description;

    /** 订单金额 */
    private BigDecimal amount;

    /** 货币代码 */
    private String currency;

    /** 订单状态 */
    private Integer status;

    /** 订单状态名称 */
    private String statusName;

    /** 订单过期时间 */
    private LocalDateTime expiredAt;

    /** 支付完成时间 */
    private LocalDateTime paidAt;

    /** 取消时间 */
    private LocalDateTime cancelledAt;

    /** 退款时间 */
    private LocalDateTime refundedAt;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 支付平台 */
    private String paymentPlatform;

    /** 支付平台名称 */
    private String paymentPlatformName;

    /** 支付类型 */
    private String paymentType;

    /** 支付类型名称 */
    private String paymentTypeName;

    /** 第三方支付平台的订单ID */
    private String providerOrderId;

    /** 订单扩展信息 */
    private Map<String, Object> metadata;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public OrderDTO() {
    }
}
