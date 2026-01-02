package com.sky.AgentCore.dto.trace;

import lombok.Data;

import java.time.LocalDateTime;

/** Agent执行链路详细记录DTO */
@Data
public class AgentExecutionDetailDTO {

    /** 追踪ID */
    private String traceId;

    /** 统一的消息内容 */
    private String messageContent;

    /** 消息类型 */
    private String messageType;

    /** 此次使用的模型部署名称 */
    private String modelEndpoint;

    /** 提供商名称 */
    private String providerName;

    /** 消息Token数 */
    private Integer messageTokens;

    /** 模型调用耗时(毫秒) */
    private Integer modelCallTime;

    /** 工具名称 */
    private String toolName;

    /** 工具调用入参 */
    private String toolRequestArgs;

    /** 工具调用出参 */
    private String toolResponseData;

    /** 工具执行耗时(毫秒) */
    private Integer toolExecutionTime;

    /** 工具执行是否成功 */
    private Boolean toolSuccess;

    /** 是否触发了降级 */
    private Boolean isFallbackUsed;

    /** 降级原因 */
    private String fallbackReason;

    /** 降级前的模型部署名称 */
    private String fallbackFromEndpoint;

    /** 降级后的模型部署名称 */
    private String fallbackToEndpoint;

    /** 降级前的服务商名称 */
    private String fallbackFromProvider;

    /** 降级后的服务商名称 */
    private String fallbackToProvider;

    /** 步骤执行是否成功 */
    private Boolean stepSuccess;

    /** 步骤错误信息 */
    private String stepErrorMessage;

    /** 创建时间 */
    private LocalDateTime createdTime;

    public AgentExecutionDetailDTO() {
    }

}
