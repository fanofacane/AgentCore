package com.sky.AgentCore.service;

import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public interface ConversationAppService {

    SseEmitter previewAgent(AgentPreviewRequest previewRequest, String userId);
}
