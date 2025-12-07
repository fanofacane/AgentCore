package com.sky.AgentCore.dto.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** Agent实体类，代表一个AI助手 */
@Data
@TableName(value = "agents")
public class AgentEntity extends BaseEntity {

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

    /** Agent状态：1-启用，0-禁用 */
    private Boolean enabled;

    /** 创建者用户ID */
    private String userId;

    /** 预先设置工具参数，结构如下： { "<mcpServerName>":{ "toolName":"paranms" } } */
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;

    /** 是否支持多模态 */
    private Boolean multiModal;
}
