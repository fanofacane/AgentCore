package com.sky.AgentCore.dto.agent.widget;


import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ProviderDTO;
import com.sky.AgentCore.enums.WidgetType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** Agent小组件配置DTO */
@Data
public class AgentWidgetDTO {

    /** 主键ID */
    private String id;

    /** Agent ID */
    private String agentId;

    /** 创建者用户ID */
    private String userId;

    /** Widget访问的唯一ID */
    private String publicId;

    /** Widget名称 */
    private String name;

    /** Widget描述 */
    private String description;

    /** 关联的模型信息 */
    private ModelDTO model;

    /** 关联的服务商信息 */
    private ProviderDTO provider;

    /** 允许的域名列表 */
    private List<String> allowedDomains;

    /** 每日调用限制（-1为无限制） */
    private Integer dailyLimit;

    /** 是否启用 */
    private Boolean enabled;

    /** Widget类型：AGENT/RAG */
    private WidgetType widgetType;

    /** 知识库ID列表（RAG类型专用） */
    private List<String> knowledgeBaseIds;

    /** Widget嵌入代码 */
    private String widgetCode;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
