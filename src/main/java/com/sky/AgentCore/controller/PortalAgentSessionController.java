package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.agent.AgentPreviewRequest;
import com.sky.AgentCore.dto.chat.ChatRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.message.MessageDTO;
import com.sky.AgentCore.dto.session.SessionDTO;
import com.sky.AgentCore.service.agent.AgentSessionService;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/agents/sessions")
public class PortalAgentSessionController {
    @Autowired
    private ConversationAppService conversationAppService;
    @Autowired
    private AgentSessionService agentSessionAppService;
    /** Agent预览功能 用于在创建/编辑Agent时预览对话效果，无需保存会话
     * @param previewRequest 预览请求对象
     * @return SSE流 */
    @PostMapping("/preview")
    public SseEmitter preview(@RequestBody AgentPreviewRequest previewRequest) {
        String userId = UserContext.getCurrentUserId();
        return conversationAppService.previewAgent(previewRequest, userId);
    }
    /** 发送消息
     * @param chatRequest 消息对象
     * @return */
    @PostMapping("/chat")
    public SseEmitter chat(@RequestBody @Validated ChatRequest chatRequest) {
        return conversationAppService.chat(chatRequest, UserContext.getCurrentUserId());
    }
    /** 创建会话 */
    @PostMapping("/{agentId}")
    public Result<SessionDTO> createSession(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentSessionAppService.createSession(userId, agentId));
    }
    /** 获取助理会话列表 */
    @GetMapping("/{agentId}")
    public Result<List<SessionDTO>> getAgentSessionList(@PathVariable String agentId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(agentSessionAppService.getAgentSessionList(userId, agentId));
    }
    /** 获取会话中的消息列表 */
    @GetMapping("/{sessionId}/messages")
    public Result<List<MessageDTO>> getConversationMessages(@PathVariable String sessionId) {
        String userId = UserContext.getCurrentUserId();
        return Result.success(conversationAppService.getConversationMessages(sessionId, userId));
    }
    /** 更新会话 */
    @PutMapping("/{id}")
    public Result<Void> updateSession(@PathVariable String id, @RequestParam String title) {
        String userId = UserContext.getCurrentUserId();
        agentSessionAppService.updateSession(id, userId, title);
        return Result.success();
    }
    /** 删除会话 */
    @DeleteMapping("/{id}")
    public Result<Void> deleteSession(@PathVariable String id) {
        String userId = UserContext.getCurrentUserId();
        agentSessionAppService.deleteSession(id, userId);
        return Result.success();
    }
}
