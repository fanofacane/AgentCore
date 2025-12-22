package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.rag.RagQaDatasetDTO;
import com.sky.AgentCore.dto.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Agent知识库管理控制器 用于Agent配置中的知识库选择和管理 */
@RestController
@RequestMapping("/agents/knowledge-bases")
public class AgentKnowledgeBaseController {
    /** 获取用户可用的知识库列表（用于Agent配置） 只返回已安装的知识库（用户创建的知识库会自动安装）
     * @return 知识库列表 */
    @GetMapping("/available")
    public Result<List<RagQaDatasetDTO>> getAvailableKnowledgeBases() {
//        String userId = UserContext.getCurrentUserId();
//        List<RagQaDatasetDTO> datasets = ragQaDatasetAppService.getUserAvailableDatasets(userId);
        return Result.success(List.of());
    }
}
