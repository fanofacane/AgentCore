package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController
@RequestMapping("/agents/sessions")
public class PortalAgentSessionController {
    @Autowired
    private ConversationAppService conversationAppService;
    /** Agent预览功能 用于在创建/编辑Agent时预览对话效果，无需保存会话
     * @param previewRequest 预览请求对象
     * @return SSE流 */
    @PostMapping("/preview")
    public SseEmitter preview(@RequestBody AgentPreviewRequest previewRequest) {
        String userId = UserContext.getCurrentUserId();
        return conversationAppService.previewAgent(previewRequest, userId);
    }
}
