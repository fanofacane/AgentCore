package com.sky.AgentCore.dto.agent;

import com.sky.AgentCore.dto.message.MessageDTO;
import com.sky.AgentCore.dto.model.LLMModelConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Agent预览请求DTO 用于预览尚未创建的Agent的对话效果 */
@Data
public class AgentPreviewRequest {

    /** 用户当前输入的消息 */
    private String userMessage;

    /** 系统提示词 */
    private String systemPrompt;

    /** 工具ID列表 */
    private List<String> toolIds;

    /** 工具预设参数 */
    private Map<String, Map<String, Map<String, String>>> toolPresetParams;

    /** 历史消息上下文 */
    private List<MessageDTO> messageHistory;

    /** 使用的模型ID，如果为空则使用用户默认模型 */
    private String modelId;

    private LLMModelConfig llmModelConfig;

    /** 文件列表 */
    private List<String> fileUrls = new ArrayList<>();

    /** 知识库ID列表，用于RAG功能 */
    private List<String> knowledgeBaseIds = new ArrayList<>();

}