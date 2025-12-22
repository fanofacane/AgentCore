package com.sky.AgentCore.dto.tool;

import lombok.Data;

import java.time.LocalDateTime;

/** 任务数据传输对象 */
@Data
public class TaskDTO {
    /**
     * 任务ID
     */
    private String id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 父任务ID
     */
    private String parentTaskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 任务进度
     */
    private Integer progress;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
