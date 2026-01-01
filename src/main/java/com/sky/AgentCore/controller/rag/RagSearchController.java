package com.sky.AgentCore.controller.rag;

import com.sky.AgentCore.dto.chat.RagSearchRequest;
import com.sky.AgentCore.dto.chat.RagStreamChatRequest;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.rag.DocumentUnitDTO;
import com.sky.AgentCore.service.chat.ConversationAppService;
import com.sky.AgentCore.service.rag.service.search.RAGSearchAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/** RAG搜索控制器 */
@RestController
@RequestMapping("/rag/search")
public class RagSearchController {
    @Autowired
    private RAGSearchAppService ragSearchAppService;
    @Autowired
    private ConversationAppService conversationAppService;
    /** RAG搜索文档
     *
     * @param request RAG搜索请求
     * @return 搜索结果 */
    @PostMapping
    public Result<List<DocumentUnitDTO>> ragSearch(@RequestBody @Validated RagSearchRequest request) {
        String userId = UserContext.getCurrentUserId();
        List<DocumentUnitDTO> searchResults = ragSearchAppService.ragSearch(request, userId);
        return Result.success(searchResults);
    }
    /** RAG流式问答 - 使用统一架构
     *
     * @param request 流式问答请求
     * @return 流式响应 */
    @PostMapping("/stream-chat")
    public SseEmitter ragStreamChat(@RequestBody @Validated RagStreamChatRequest request) {
        String userId = UserContext.getCurrentUserId();
        return conversationAppService.ragStreamChat(request, userId);
    }
    /** 基于已安装知识库的RAG搜索
     *
     * @param userRagId 已安装的知识库ID
     * @param request RAG搜索请求
     * @return 搜索结果 */
    @PostMapping("/user-rag/{userRagId}")
    public Result<List<DocumentUnitDTO>> ragSearchByUserRag(@PathVariable String userRagId,
                                                            @RequestBody @Validated RagSearchRequest request) {
        String userId = UserContext.getCurrentUserId();
        List<DocumentUnitDTO> searchResults = ragSearchAppService.ragSearchByUserRag(request, userRagId, userId);
        return Result.success(searchResults);
    }

    /** 基于已安装知识库的RAG流式问答 - 使用统一架构
     *
     * @param userRagId 已安装的知识库ID
     * @param request 流式问答请求
     * @return 流式响应 */
    @PostMapping("/user-rag/{userRagId}/stream-chat")
    public SseEmitter ragStreamChatByUserRag(@PathVariable String userRagId,
                                             @RequestBody @Validated RagStreamChatRequest request) {
        String userId = UserContext.getCurrentUserId();
        return conversationAppService.ragStreamChatByUserRag(request, userRagId, userId);
    }
}
