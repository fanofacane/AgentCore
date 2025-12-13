package com.sky.AgentCore.dto.agent;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Agent版本数据传输对象，用于表示层和应用层之间传递Agent版本数据 */
@Data
public class AgentVersionDTO {

    /**
     * 版本唯一ID
     */
    private String id;

    /**
     * 关联的Agent ID
     */
    private String agentId;

    /**
     * Agent名称
     */
    private String name;

    /**
     * Agent头像URL
     */
    private String avatar;

    /**
     * Agent描述
     */
    private String description;

    /**
     * 版本号，如1.0.0
     */
    private String versionNumber;

    /**
     * Agent系统提示词
     */
    private String systemPrompt;

    /**
     * 欢迎消息
     */
    private String welcomeMessage;

    /**
     * Agent可使用的工具列表
     */
    private List<String> toolIds;

    /**
     * 关联的知识库ID列表
     */
    private List<String> knowledgeBaseIds;

    /**
     * 版本更新日志
     */
    private String changeLog;

    /**
     * 发布状态：1-审核中, 2-已发布, 3-拒绝, 4-已下架
     */
    private Integer publishStatus;

    /**
     * 审核拒绝原因
     */
    private String rejectReason;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建者用户ID
     */
    private String userId;

    /**
     * 是否已添加到工作区
     */
    private Boolean isAddWorkspace;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 无参构造函数
     */
    public AgentVersionDTO() {
        this.toolIds = new ArrayList<>();
        this.knowledgeBaseIds = new ArrayList<>();
    }
}