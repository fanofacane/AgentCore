package com.sky.AgentCore.dto.chat;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/** 追踪上下文 用于在执行过程中传递追踪信息 */
@Data
public class TraceContext {

    /** 用户ID */
    private final String userId;

    /** 会话ID */
    private final String sessionId;

    /** Agent ID */
    private final String agentId;

    /** 执行开始时间 */
    private final LocalDateTime startTime;

    /** 序列号生成器 */
    private final AtomicInteger sequenceGenerator;

    /** 是否启用追踪 */
    private final boolean traceEnabled;

    /** 用户消息内容 */
    private String userMessage;

    /** 用户消息类型 */
    private String userMessageType;

    /** 当前用户消息记录ID（用于后续更新Token） */
    private Long currentUserMessageId;

    public TraceContext(String userId, String sessionId, String agentId, boolean traceEnabled) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.agentId = agentId;
        this.startTime = LocalDateTime.now();
        this.sequenceGenerator = new AtomicInteger(0);
        this.traceEnabled = traceEnabled;
    }

    /** 创建追踪上下文 */
    public static TraceContext create(String userId, String sessionId, String agentId) {
        return new TraceContext(userId, sessionId, agentId, true);
    }

    /** 创建禁用追踪的上下文 */
    public static TraceContext createDisabled() {
        return new TraceContext(null, null, null, false);
    }

    /** 生成下一个序列号 */
    public int nextSequence() {
        return sequenceGenerator.incrementAndGet();
    }

    /** 获取当前序列号 */
    public int getCurrentSequence() {
        return sequenceGenerator.get();
    }

    public String getTraceId() {
        return sessionId; // 使用sessionId作为traceId
    }
    @Override
    public String toString() {
        return "TraceContext{" + "sessionId='" + sessionId + '\'' + ", userId=" + userId + ", agentId=" + agentId
                + ", startTime=" + startTime + ", currentSequence=" + sequenceGenerator.get() + ", traceEnabled="
                + traceEnabled + '}';
    }
}
