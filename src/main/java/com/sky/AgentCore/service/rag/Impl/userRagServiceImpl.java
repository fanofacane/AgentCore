package com.sky.AgentCore.service.rag.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.rag.UserRagEntity;
import com.sky.AgentCore.mapper.UserRagMapper;
import com.sky.AgentCore.service.rag.UserRagService;
import org.springframework.stereotype.Service;

@Service
public class userRagServiceImpl extends ServiceImpl<UserRagMapper, UserRagEntity> implements UserRagService {
    @Override
    public boolean isRagInstalledByOriginalId(String userId, String originalRagId) {
        return findInstalledRagByOriginalId(userId, originalRagId) != null;
    }
    /** 查找用户安装的RAG（按原始RAG ID）
     *
     * @param userId 用户ID
     * @param originalRagId 原始RAG数据集ID
     * @return 安装记录，如果未安装则返回null */
    @Override
    public UserRagEntity findInstalledRagByOriginalId(String userId, String originalRagId) {
        return lambdaQuery().eq(UserRagEntity::getUserId, userId).eq(UserRagEntity::getOriginalRagId, originalRagId).one();
    }
}
