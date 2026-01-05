package com.sky.AgentCore.dto.account;

import com.sky.AgentCore.dto.tool.Page;
import lombok.Data;

@Data
public class QueryUserOrderRequest extends Page {
    /** 订单类型（可选） */
    private String orderType;

    /** 订单状态（可选） */
    private Integer status;
}
