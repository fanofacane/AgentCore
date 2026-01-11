package com.sky.AgentCore.dto.scheduledtask;


import com.sky.AgentCore.dto.config.RepeatConfig;
import com.sky.AgentCore.enums.RepeatType;
import com.sky.AgentCore.enums.ScheduleTaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 更新定时任务请求 */
@Data
public class UpdateScheduledTaskRequest {

    /** 任务ID */
    @NotBlank(message = "任务ID不能为空")
    private String id;

    /** 任务内容 */
    private String content;

    /** 重复类型 */
    private RepeatType repeatType;

    /** 重复配置 */
    private RepeatConfig repeatConfig;

    /** 任务状态 */
    private ScheduleTaskStatus status;

}
