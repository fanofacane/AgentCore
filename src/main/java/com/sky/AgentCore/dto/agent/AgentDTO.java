package com.sky.AgentCore.dto.agent;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Agent数据传输对象，用于表示层和应用层之间传递数据 */
@Data
public class AgentDTO {

    /** Agent唯一ID */
    private String id;

    /** Agent名称 */
    private String name;

    /** Agent头像URL */
    private String avatar;

    /** Agent描述 */
    private String description;

    /** Agent系统提示词 */
    private String systemPrompt;

    /** 欢迎消息 */
    private String welcomeMessage;

    /** Agent可使用的工具列表 */
    private List<String> toolIds;

    /** 关联的知识库ID列表 */
    private List<String> knowledgeBaseIds;

    /** 当前发布的版本ID */
    private String publishedVersion;

    /** Agent状态：true-启用，false-禁用 */
    private Boolean enabled = Boolean.TRUE;

    /** 创建者用户ID */
    private String userId;

    /** 创建者用户昵称 */
    private String userNickname;

    private Map<String, Map<String, Map<String, String>>> toolPresetParams;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;

    /** 是否支持多模态 */
    private Boolean multiModal;

    /** 无参构造函数 */
    public AgentDTO() {
        this.toolIds = new ArrayList<>();
        this.knowledgeBaseIds = new ArrayList<>();
    }
}
