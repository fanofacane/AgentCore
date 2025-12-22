package com.sky.AgentCore.dto.gateway;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** 上报调用结果请求
 *
 * @author fanofacane
 * @since 1.0.0 */
@Data
@NoArgsConstructor
public class ReportResultRequest {

    /** 用户ID，可选 */
    private String userId;

    /** API实例ID，必填 */
    private String instanceId;

    /** 业务ID，必填 */
    private String businessId;

    /** 调用是否成功，必填 */
    private Boolean success;

    /** 调用延迟（毫秒），必填 */
    private Long latencyMs;

    /** 错误信息，失败时可选 */
    private String errorMessage;

    /** 错误类型，失败时可选 */
    private String errorType;

    /** 使用指标，可选 */
    private Map<String, Object> usageMetrics;

    /** 调用时间戳，必填 */
    private Long callTimestamp;
}
