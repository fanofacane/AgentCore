package com.sky.AgentCore.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sky.AgentCore.dto.rag.UserRagEntity;
import com.sky.AgentCore.mapper.UserRagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRagService {
    @Autowired
    private UserRagMapper userRagMapper;
    public boolean isRagInstalledByOriginalId(String userId, String originalRagId) {
        return findInstalledRagByOriginalId(userId, originalRagId) != null;
    }
    /** 查找用户安装的RAG（按原始RAG ID）
     *
     * @param userId 用户ID
     * @param originalRagId 原始RAG数据集ID
     * @return 安装记录，如果未安装则返回null */

    public UserRagEntity findInstalledRagByOriginalId(String userId, String originalRagId) {
        LambdaQueryWrapper<UserRagEntity> wrapper = Wrappers.<UserRagEntity>lambdaQuery()
                .eq(UserRagEntity::getUserId, userId)
                .eq(UserRagEntity::getOriginalRagId, originalRagId)
                .last("limit 1");
        return userRagMapper.selectOne(wrapper);
    }
}
