package com.sky.AgentCore.dto.agent;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.ListConverter;
import com.sky.AgentCore.converter.MapConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.PublishStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Agent版本实体类，代表一个Agent的发布版本 */
@Data
@TableName(value = "agent_versions", autoResultMap = true)
public class AgentVersionEntity extends BaseEntity {

    /** 版本唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 关联的Agent ID */
    @TableField("agent_id")
    private String agentId;

    /** Agent名称 */
    @TableField("name")
    private String name;

    /** Agent头像URL */
    @TableField("avatar")
    private String avatar;

    /** Agent描述 */
    @TableField("description")
    private String description;

    /** 版本号，如1.0.0 */
    @TableField("version_number")
    private String versionNumber;

    /** Agent系统提示词 */
    @TableField("system_prompt")
    private String systemPrompt;

    /** 欢迎消息 */
    @TableField("welcome_message")
    private String welcomeMessage;

    /** Agent可使用的工具列表 */
    @TableField(value = "tool_ids", typeHandler = ListConverter.class)
    private List<String> toolIds;

    /** 关联的知识库ID列表 */
    @TableField(value = "knowledge_base_ids", typeHandler = ListConverter.class)
    private List<String> knowledgeBaseIds;

    /** 版本更新日志 */
    @TableField("change_log")
    private String changeLog;

    /** 发布状态：1-审核中, 2-已发布, 3-拒绝, 4-已下架 */
    @TableField("publish_status")
    private Integer publishStatus;

    /** 审核拒绝原因 */
    @TableField("reject_reason")
    private String rejectReason;

    /** 审核时间 */
    @TableField("review_time")
    private LocalDateTime reviewTime;

    /** 发布时间 */
    @TableField("published_at")
    private LocalDateTime publishedAt;

    /** 创建者用户ID */
    @TableField("user_id")
    private String userId;

    /** 预先设置的工具参数 */
    @TableField(value = "tool_preset_params", typeHandler = MapConverter.class)
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;

    /** 是否支持多模态 */
    @TableField("multi_modal")
    private Boolean multiModal;

    /** 无参构造函数 */
    public AgentVersionEntity() {
        this.toolIds = new ArrayList<>();
        this.knowledgeBaseIds = new ArrayList<>();
    }
    /** 获取发布状态枚举 */
    public PublishStatus getPublishStatusEnum() {
        return PublishStatus.fromCode(this.publishStatus);
    }
    /** 更新发布状态 */
    public void updatePublishStatus(PublishStatus status) {
        this.publishStatus = status.getCode();
        this.reviewTime = LocalDateTime.now();
    }

    /** 拒绝发布 */
    public void reject(String reason) {
        this.publishStatus = PublishStatus.REJECTED.getCode();
        this.rejectReason = reason;
        this.reviewTime = LocalDateTime.now();
    }

    /** 从Agent实体创建一个新的版本实体 */
    public static AgentVersionEntity createFromAgent(AgentEntity agent, String versionNumber, String changeLog) {
        AgentVersionEntity version = new AgentVersionEntity();
        version.setAgentId(agent.getId());
        version.setName(agent.getName());
        version.setAvatar(agent.getAvatar());
        version.setDescription(agent.getDescription());
        version.setVersionNumber(versionNumber);
        version.setSystemPrompt(agent.getSystemPrompt());
        version.setWelcomeMessage(agent.getWelcomeMessage());
        version.setToolIds(agent.getToolIds());
        version.setKnowledgeBaseIds(agent.getKnowledgeBaseIds());
        version.setChangeLog(changeLog);
        version.setUserId(agent.getUserId());
        version.setMultiModal(agent.getMultiModal());
        // 创建时间和发布时间应该相同
        LocalDateTime now = LocalDateTime.now();
        version.setCreatedAt(now);
        version.setUpdatedAt(now);
        version.setPublishedAt(now);

        // 设置初始状态为审核中
        version.setPublishStatus(PublishStatus.REVIEWING.getCode());
        version.setReviewTime(now);
        version.setToolPresetParams(agent.getToolPresetParams());
        return version;
    }
}
