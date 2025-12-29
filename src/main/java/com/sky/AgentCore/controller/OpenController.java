package com.sky.AgentCore.controller;

import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.chat.ChatRequest;
import com.sky.AgentCore.dto.chat.ChatResponse;
import com.sky.AgentCore.dto.chat.ExternalChatRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.session.ExternalCreateSessionRequest;
import com.sky.AgentCore.dto.session.SessionDTO;
import com.sky.AgentCore.dto.enums.ModelType;
import com.sky.AgentCore.dto.enums.ProviderType;
import com.sky.AgentCore.service.agent.AgentSessionService;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.service.llm.LLMAppService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.utils.ExternalApiContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 外部API控制器 提供给外部系统的API接口，使用API Key进行身份验证 */
@RestController
@RequestMapping("/v1")
public class OpenController {
    @Autowired
    private LLMDomainService llmDomainService;
    @Autowired
    private ConversationAppService conversationAppService;
    @Autowired
    private LLMAppService llmAppService;
    @Autowired
    private AgentSessionService agentSessionService;
    /** 发起对话
     * @param request 聊天请求
     * @return 流式或同步响应 */
    @PostMapping("/chat/completions")
    public Object chat(@RequestBody @Validated ExternalChatRequest request) {
        String userId = ExternalApiContext.getUserId();
        String agentId = ExternalApiContext.getAgentId();
        if (request.getSessionId()== null){
            SessionDTO session = agentSessionService.createSession(userId, agentId);
            request.setSessionId(session.getId());
        }
        // 异常分支：如果指定了模型但无权限使用
        if (request.getModel() != null && !llmDomainService.canUserUseModel(request.getModel(), userId)) {
            throw new BusinessException("无权限使用指定模型: " + request.getModel());
        }

        // 主流程：构建请求并处理对话
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessage(request.getMessage());
        chatRequest.setSessionId(request.getSessionId());
        chatRequest.setFileUrls(request.getFiles());

        // 根据stream参数选择返回类型
        if (request.getStream() != null && request.getStream()) {
            // 流式响应 - 直接返回SseEmitter，Spring Boot会自动处理响应头
            return conversationAppService.chatWithModel(chatRequest, userId, request.getModel());
        } else {
            // 同步响应
            ChatResponse response = conversationAppService.chatSyncWithModel(chatRequest, userId, request.getModel());
            return Result.success(response);
        }
    }
    /** 获取可用模型列表
     * @return 模型列表 */
    @GetMapping("/models")
    public Result<List<ModelDTO>> getAvailableModels() {
        String userId = ExternalApiContext.getUserId();
        return Result.success(llmAppService.getActiveModelsByType(ProviderType.ALL, userId, ModelType.CHAT));
    }
    /** 创建新会话
     * @param request 创建会话请求
     * @return 会话信息 */
    @PostMapping("/sessions")
    public Result<SessionDTO> createSession(@RequestBody ExternalCreateSessionRequest request) {
        String userId = ExternalApiContext.getUserId();
        String agentId = ExternalApiContext.getAgentId();
        SessionDTO session = agentSessionService.createSession(userId, agentId);

        // 如果指定了标题，更新标题
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            agentSessionService.updateSession(session.getId(), userId, request.getTitle().trim());
            session.setTitle(request.getTitle().trim());
        }

        return Result.success(session);
    }

    /** 删除会话
     * @param id 会话ID
     * @return 操作结果 */
    @DeleteMapping("/sessions/{id}")
    public Result<Void> deleteSession(@PathVariable String id) {
        String userId = ExternalApiContext.getUserId();
        agentSessionService.deleteSession(id, userId);
        return Result.success();
    }
}
