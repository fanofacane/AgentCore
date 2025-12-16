package com.sky.AgentCore.service.chat;

import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import com.sky.AgentCore.dto.chat.ChatRequest;
import com.sky.AgentCore.dto.message.MessageDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


public interface ConversationAppService {

    SseEmitter previewAgent(AgentPreviewRequest previewRequest, String userId);

    List<MessageDTO> getConversationMessages(String sessionId, String userId);

    SseEmitter chat(ChatRequest chatRequest, String currentUserId);

    SseEmitter chatWithModel(ChatRequest chatRequest, String userId, String model);
}
