package com.sky.AgentCore.dto.scheduledtask;


import com.sky.AgentCore.dto.config.RepeatConfig;
import com.sky.AgentCore.enums.RepeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 创建定时任务请求 */
@Data
public class CreateScheduledTaskRequest {

    /** Agent ID */
    @NotBlank(message = "Agent ID不能为空")
    private String agentId;

    /** 会话ID */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /** 任务内容 */
    @NotBlank(message = "任务内容不能为空")
    private String content;

    /** 重复类型 */
    @NotNull(message = "重复类型不能为空")
    private RepeatType repeatType;

    /** 重复配置 */
    @NotNull(message = "重复配置不能为空")
    private RepeatConfig repeatConfig;

}
