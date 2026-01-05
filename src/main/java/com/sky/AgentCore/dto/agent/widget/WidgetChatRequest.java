package com.sky.AgentCore.dto.agent.widget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/** 小组件聊天请求 */
@Data
public class WidgetChatRequest {

    /** 用户消息 */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 4000, message = "消息内容长度不能超过4000字符")
    private String message;

    /** 匿名会话ID */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /** 文件URL列表 */
    private List<String> fileUrls;

}
