package com.sky.AgentCore.service.rag;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.rag.UserRagEntity;

public interface UserRagService extends IService<UserRagEntity> {
    boolean isRagInstalledByOriginalId(String userId, String knowledgeBaseId);

    UserRagEntity findInstalledRagByOriginalId(String userId, String knowledgeBaseId);
}
