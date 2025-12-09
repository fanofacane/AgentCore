package com.sky.AgentCore.dto.billing;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/** 规则上下文 封装计费所需的所有信息 */
@Data
@Builder
public class RuleContext {

    /** 规则类型 (如：MODEL_USAGE, AGENT_CREATION) */
    private String type;

    /** 服务ID (业务标识，如模型ID、固定标识等) */
    private String serviceId;

    /** 用量数据 (如：{"input": 1000, "output": 500}) */
    private Map<String, Object> usageData;

    /** 请求ID，用于幂等性控制 */
    private String requestId;

    /** 用户ID */
    private String userId;


    /** 验证上下文数据是否有效 */
    public boolean isValid() {
        return type != null && !type.trim().isEmpty() && serviceId != null && !serviceId.trim().isEmpty()
                && usageData != null && !usageData.isEmpty() && userId != null && !userId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "BillingContext{" + "type='" + type + '\'' + ", serviceId='" + serviceId + '\'' + ", usageData="
                + usageData + ", requestId='" + requestId + '\'' + ", userId='" + userId + '\'' + '}';
    }
}
