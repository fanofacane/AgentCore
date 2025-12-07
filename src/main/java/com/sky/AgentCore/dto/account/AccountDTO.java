package com.sky.AgentCore.dto.account;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 账户DTO 用户账户信息传输对象 */
@Data
public class AccountDTO {

    /** 账户ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 账户余额 */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
    private BigDecimal balance;

    /** 信用额度 */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
    private BigDecimal credit;

    /** 总消费金额 */
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
    private BigDecimal totalConsumed;

    /** 最后交易时间 */
    private LocalDateTime lastTransactionAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
