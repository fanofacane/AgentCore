package com.sky.AgentCore.service.rag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.rag.RagVersionEntity;

import java.util.List;

public interface RagVersionService extends IService<RagVersionEntity> {
    List<RagVersionEntity> getVersionsByOriginalRagId(String knowledgeBaseId, String userId);

    RagVersionEntity getRagVersion(String ragVersionId);
}
