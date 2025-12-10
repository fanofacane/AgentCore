package com.sky.AgentCore.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** RAG专用的聊天上下文 继承ChatContext，添加RAG特定的配置 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RagChatContext extends ChatContext {

    /** RAG搜索请求配置 */
    private RagSearchRequest ragSearchRequest;

    /** 用户RAG ID（已安装的知识库ID，可选） */
    private String userRagId;

    /** 文件ID（可选，用于单文件检索） */
    private String fileId;
}
