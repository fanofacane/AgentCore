package com.sky.AgentCore.dto.agent;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ListStringConverter;
import com.sky.AgentCore.converter.MapConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Agent实体类，代表一个AI助手 */
@Data
@TableName(value = "agents")
public class AgentEntity extends BaseEntity {

    /** Agent唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** Agent名称 */
    @TableField("name")
    private String name;

    /** Agent头像URL */
    @TableField("avatar")
    private String avatar;

    /** Agent描述 */
    @TableField("description")
    private String description;

    /** Agent系统提示词 */
    @TableField("system_prompt")
    private String systemPrompt;

    /** 欢迎消息 */
    @TableField("welcome_message")
    private String welcomeMessage;

    /** Agent可使用的工具列表 */
    @TableField(value = "tool_ids", typeHandler = ListStringConverter.class)
    private List<String> toolIds;

    /** 关联的知识库ID列表 */
    @TableField(value = "knowledge_base_ids", typeHandler = ListStringConverter.class)
    private List<String> knowledgeBaseIds;

    /** 当前发布的版本ID */
    @TableField("published_version")
    private String publishedVersion;

    /** Agent状态：1-启用，0-禁用 */
    @TableField("enabled")
    private Boolean enabled;

    /** 创建者用户ID */
    @TableField("user_id")
    private String userId;

    /** 预先设置工具参数，结构如下： { "<mcpServerName>":{ "toolName":"paranms" } } */
    @TableField(value = "tool_preset_params", typeHandler = MapConverter.class)
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;

    /** 是否支持多模态 */
    @TableField("multi_modal")
    private Boolean multiModal;

    /** 无参构造函数 */
    public AgentEntity() {
        this.toolIds = new ArrayList<>();
        this.knowledgeBaseIds = new ArrayList<>();
    }
    /** 创建新的Agent对象 */
    public static AgentEntity createNew(String name, String description, String avatar, String userId) {
        AgentEntity agent = new AgentEntity();
        agent.setName(name);
        agent.setDescription(description);
        agent.setAvatar(avatar);
        agent.setUserId(userId);
        agent.setEnabled(true); // 默认启用
        agent.setCreatedAt(LocalDateTime.now());
        agent.setUpdatedAt(LocalDateTime.now());
        return agent;
    }

    /** 更新Agent基本信息 */
    public void updateBasicInfo(String name, String avatar, String description) {
        this.name = name;
        this.avatar = avatar;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /** 启用Agent */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /** 禁用Agent */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /** 发布新版本 */
    public void publishVersion(String versionId) {
        this.publishedVersion = versionId;
        this.updatedAt = LocalDateTime.now();
    }

    /** 软删除 */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void isEnable() {
        if (!this.enabled) {
            throw new BusinessException("助理未激活");
        }
    }
}
