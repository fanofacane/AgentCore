package com.sky.AgentCore.dto.trace;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/** Agent执行链路汇总实体 记录每次Agent执行的整体信息和汇总数据 */
@Data
@TableName("agent_execution_summary")
public class AgentExecutionSummaryEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * Agent ID
     */
    @TableField("agent_id")
    private String agentId;

    /**
     * 执行开始时间
     */
    @TableField("execution_start_time")
    private LocalDateTime executionStartTime;

    /**
     * 执行结束时间
     */
    @TableField("execution_end_time")
    private LocalDateTime executionEndTime;

    /**
     * 总执行时间(毫秒)
     */
    @TableField("total_execution_time")
    private Integer totalExecutionTime;

    /**
     * 总输入Token数
     */
    @TableField("total_input_tokens")
    private Integer totalInputTokens;

    /**
     * 总输出Token数
     */
    @TableField("total_output_tokens")
    private Integer totalOutputTokens;

    /**
     * 总Token数
     */
    @TableField("total_tokens")
    private Integer totalTokens;

    /**
     * 工具调用总次数
     */
    @TableField("tool_call_count")
    private Integer toolCallCount;

    /**
     * 工具执行总耗时(毫秒)
     */
    @TableField("total_tool_execution_time")
    private Integer totalToolExecutionTime;

    /**
     * 执行是否成功
     */
    @TableField("execution_success")
    private Boolean executionSuccess;

    /**
     * 错误发生阶段
     */
    @TableField("error_phase")
    private String errorPhase;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    public AgentExecutionSummaryEntity() {
        this.totalInputTokens = 0;
        this.totalOutputTokens = 0;
        this.totalTokens = 0;
        this.toolCallCount = 0;
        this.totalToolExecutionTime = 0;
    }

    /**
     * 创建新的执行追踪汇总
     */
    public static AgentExecutionSummaryEntity create(String userId, String sessionId, String agentId) {
        AgentExecutionSummaryEntity entity = new AgentExecutionSummaryEntity();
        entity.setUserId(userId);
        entity.setSessionId(sessionId);
        entity.setAgentId(agentId);
        entity.setExecutionStartTime(LocalDateTime.now());
        entity.setExecutionSuccess(false); // 默认为失败，执行完成后设置为成功
        return entity;
    }

    /**
     * 标记执行完成
     */
    public void markCompleted(boolean success, String errorPhase, String errorMessage) {
        this.executionEndTime = LocalDateTime.now();
        this.executionSuccess = success;
        this.errorPhase = errorPhase;
        this.errorMessage = errorMessage;

        // 计算总执行时间
        if (this.executionStartTime != null && this.executionEndTime != null) {
            long duration = java.time.Duration.between(this.executionStartTime, this.executionEndTime).toMillis();
            this.totalExecutionTime = (int) duration;
        }
    }

    /**
     * 添加Token统计
     */
    public void addTokens(Integer inputTokens, Integer outputTokens) {
        if (inputTokens != null) {
            this.totalInputTokens += inputTokens;
        }
        if (outputTokens != null) {
            this.totalOutputTokens += outputTokens;
        }
        this.totalTokens = this.totalInputTokens + this.totalOutputTokens;
    }

    /**
     * 添加工具调用信息
     */
    public void addToolExecution(Integer executionTime) {
        this.toolCallCount++;
        if (executionTime != null) {
            this.totalToolExecutionTime += executionTime;
        }
    }

    /**
     * 检查是否需要检查用户ID权限
     */
    public boolean needCheckUserId() {
        return true; // Agent执行追踪需要用户权限检查
    }
}
