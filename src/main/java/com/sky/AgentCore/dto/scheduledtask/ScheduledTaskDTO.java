package com.sky.AgentCore.dto.scheduledtask;


import com.sky.AgentCore.dto.config.RepeatConfig;
import com.sky.AgentCore.enums.RepeatType;
import com.sky.AgentCore.enums.ScheduleTaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

/** 定时任务DTO */
@Data
public class ScheduledTaskDTO {

    /** 任务ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** Agent ID */
    private String agentId;

    /** 会话ID */
    private String sessionId;

    /** 任务内容 */
    private String content;

    /** 重复类型 */
    private RepeatType repeatType;

    /** 重复配置 */
    private RepeatConfig repeatConfig;

    /** 任务状态 */
    private ScheduleTaskStatus status;

    /** 上次执行时间 */
    private LocalDateTime lastExecuteTime;

    /** 下次执行时间 */
    private LocalDateTime nextExecuteTime;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
