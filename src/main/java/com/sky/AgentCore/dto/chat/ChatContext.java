package com.sky.AgentCore.dto.chat;

import com.sky.AgentCore.dto.*;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import lombok.Data;

import java.util.List;

/** chat 上下文，包含对话所需的所有信息 */
@Data
public class ChatContext {
    /** 会话ID */
    private String sessionId;

    /** 用户ID */
    private String userId;

    /** 用户消息 */
    private String userMessage;

    /** 智能体实体 */
    private AgentEntity agent;

    /** 模型实体 */
    private ModelEntity model;

    /** 原始模型实体（用于追踪模型切换） */
    private ModelEntity originalModel;

    /** 服务商实体 */
    private ProviderEntity provider;

    /** 原始服务商实体（用于追踪服务商切换） */
    private ProviderEntity originalProvider;

    /** 大模型配置 */
    private LLMModelConfig llmModelConfig;

    /** 上下文实体 */
    private ContextEntity contextEntity;

    /** 历史消息列表 */
    private List<MessageEntity> messageHistory;

    /** 使用的 mcp server name */
    private List<String> mcpServerNames;

    /** 多模态的文件 */
    private List<String> fileUrls;

    /** 高可用实例ID */
    private String instanceId;

    /** 是否流式响应 */
    private boolean streaming = true;

    /** 追踪上下文 */
    private TraceContext traceContext;

    /** 是否为公开访问（嵌入模式） */
    private boolean publicAccess = false;

    /** 公开访问ID（嵌入模式使用） */
    private String publicId;
}
