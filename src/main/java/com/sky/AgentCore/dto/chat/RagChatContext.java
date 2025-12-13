package com.sky.AgentCore.dto.chat;



import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.message.MessageEntity;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;

import java.util.List;

/** RAG专用的聊天上下文 继承ChatContext，添加RAG特定的配置 */
public class RagChatContext extends ChatContext {

    /** RAG搜索请求配置 */
    private RagSearchRequest ragSearchRequest;

    /** 用户RAG ID（已安装的知识库ID，可选） */
    private String userRagId;

    /** 文件ID（可选，用于单文件检索） */
    private String fileId;

    public RagSearchRequest getRagSearchRequest() {
        return ragSearchRequest;
    }

    public void setRagSearchRequest(RagSearchRequest ragSearchRequest) {
        this.ragSearchRequest = ragSearchRequest;
    }

    public String getUserRagId() {
        return userRagId;
    }

    public void setUserRagId(String userRagId) {
        this.userRagId = userRagId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /** 构建器模式创建RagChatContext */
    public static RagChatContext.Builder builder() {
        return new RagChatContext.Builder();
    }

    public static class Builder {
        private RagChatContext context = new RagChatContext();

        public RagChatContext.Builder sessionId(String sessionId) {
            context.setSessionId(sessionId);
            return this;
        }

        public RagChatContext.Builder userId(String userId) {
            context.setUserId(userId);
            return this;
        }

        public RagChatContext.Builder userMessage(String userMessage) {
            context.setUserMessage(userMessage);
            return this;
        }

        public RagChatContext.Builder ragSearchRequest(RagSearchRequest ragSearchRequest) {
            context.setRagSearchRequest(ragSearchRequest);
            return this;
        }

        public RagChatContext.Builder userRagId(String userRagId) {
            context.setUserRagId(userRagId);
            return this;
        }

        public RagChatContext.Builder fileId(String fileId) {
            context.setFileId(fileId);
            return this;
        }

        public RagChatContext.Builder agent(AgentEntity agent) {
            context.setAgent(agent);
            return this;
        }

        public RagChatContext.Builder model(ModelEntity model) {
            context.setModel(model);
            return this;
        }

        public RagChatContext.Builder provider(ProviderEntity provider) {
            context.setProvider(provider);
            return this;
        }

        public RagChatContext.Builder contextEntity(ContextEntity contextEntity) {
            context.setContextEntity(contextEntity);
            return this;
        }

        public RagChatContext.Builder messageHistory(List<MessageEntity> messageHistory) {
            context.setMessageHistory(messageHistory);
            return this;
        }

        public RagChatContext.Builder instanceId(String instanceId) {
            context.setInstanceId(instanceId);
            return this;
        }

        public RagChatContext.Builder streaming(boolean streaming) {
            context.setStreaming(streaming);
            return this;
        }

        public RagChatContext build() {
            return context;
        }
    }
}

