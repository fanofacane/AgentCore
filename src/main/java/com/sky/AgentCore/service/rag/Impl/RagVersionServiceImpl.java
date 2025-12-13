package com.sky.AgentCore.service.rag.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.rag.RagVersionEntity;
import com.sky.AgentCore.enums.RagPublishStatus;
import com.sky.AgentCore.mapper.RagVersionMapper;
import com.sky.AgentCore.service.rag.RagVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagVersionServiceImpl extends ServiceImpl<RagVersionMapper, RagVersionEntity> implements RagVersionService {
    @Autowired
    private RagVersionMapper ragVersionMapper;
    @Override
    public List<RagVersionEntity> getVersionsByOriginalRagId(String originalRagId, String userId) {
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, originalRagId);
        RagVersionEntity firstVersion = lambdaQuery().eq(RagVersionEntity::getOriginalRagId, originalRagId).one();

        boolean isCreator = firstVersion != null && userId.equals(firstVersion.getUserId());
        if (!isCreator){
            // 非创建者：只显示已发布的版本
            wrapper.eq(RagVersionEntity::getPublishStatus, RagPublishStatus.PUBLISHED.getCode());
        }
        // 创建者：显示所有版本（不添加状态限制）
        wrapper.orderByAsc(RagVersionEntity::getVersion);
        return ragVersionMapper.selectList(wrapper);
    }

    @Override
    public RagVersionEntity getRagVersion(String ragVersionId) {
        RagVersionEntity ragVersion = lambdaQuery().eq(RagVersionEntity::getId, ragVersionId).one();
        if (ragVersion == null) throw new BusinessException("Rag版本不存在");
        return ragVersion;
    }
}
