package com.sky.AgentCore.service.rag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.rag.RagVersionEntity;
import com.sky.AgentCore.dto.enums.RagPublishStatus;
import com.sky.AgentCore.mapper.RagVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagVersionService {
    @Autowired
    private RagVersionMapper ragVersionMapper;

    public List<RagVersionEntity> getVersionsByOriginalRagId(String originalRagId, String userId) {
        // 检查当前用户是否为该知识库的创建者
        // 通过查询该原始RAG的任意一个版本来获取创建者信息
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, originalRagId).last("limit 1");

        RagVersionEntity firstVersion = ragVersionMapper.selectOne(wrapper);

        boolean isCreator = firstVersion != null && userId.equals(firstVersion.getUserId());
        if (!isCreator){
            // 非创建者：只显示已发布的版本
            wrapper.eq(RagVersionEntity::getPublishStatus, RagPublishStatus.PUBLISHED.getCode());
        }
        // 创建者：显示所有版本（不添加状态限制）
        wrapper.orderByAsc(RagVersionEntity::getVersion);
        return ragVersionMapper.selectList(wrapper);
    }


    public RagVersionEntity getRagVersion(String ragVersionId) {
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getId, ragVersionId);
        RagVersionEntity ragVersion = ragVersionMapper.selectOne(wrapper);
        if (ragVersion == null) throw new BusinessException("Rag版本不存在");
        return ragVersion;
    }
}
