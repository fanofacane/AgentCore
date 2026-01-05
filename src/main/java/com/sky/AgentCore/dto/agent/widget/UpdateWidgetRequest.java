package com.sky.AgentCore.dto.agent.widget;


import com.sky.AgentCore.enums.WidgetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/** 更新小组件配置请求 */
@Data
public class UpdateWidgetRequest {

    /** Widget名称 */
    @NotBlank(message = "小组件名称不能为空")
    @Size(max = 100, message = "小组件名称长度不能超过100字符")
    private String name;

    /** Widget描述 */
    @Size(max = 500, message = "小组件描述长度不能超过500字符")
    private String description;

    /** 指定使用的模型ID */
    @NotBlank(message = "请选择模型")
    private String modelId;

    /** 可选：指定服务商ID */
    private String providerId;

    /** 允许的域名列表 */
    private List<String> allowedDomains;

    /** 每日调用限制（-1为无限制） */
    @NotNull(message = "每日限制不能为空")
    private Integer dailyLimit;

    /** 是否启用 */
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;

    /** Widget类型：AGENT/RAG */
    @NotNull(message = "Widget类型不能为空")
    private WidgetType widgetType;

    /** 知识库ID列表（RAG类型专用） */
    private List<String> knowledgeBaseIds;
}
