package com.sky.AgentCore.service.rag.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.rag.*;
import com.sky.AgentCore.enums.RagPublishStatus;
import com.sky.AgentCore.mapper.rag.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RagVersionDomainService {
    @Autowired
    private RagVersionMapper ragVersionMapper;
    @Autowired
    private RagVersionFileMapper ragVersionFileMapper;
    @Autowired
    private RagVersionDocumentMapper ragVersionDocumentMapper;
    @Autowired
    private DocumentUnitMapper documentUnitMapper;
    @Autowired
    private FileDetailMapper fileDetailMapper;
    @Autowired
    private RagQaDatasetDomainService ragQaDatasetDomainService;

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

    /** 创建RAG版本快照
     *
     * @param ragId 原始RAG数据集ID
     * @param version 版本号
     * @param changeLog 更新日志
     * @param userId 用户ID
     * @return 创建的RAG版本 */
    public RagVersionEntity createRagVersionSnapshot(String ragId, String version, String changeLog, String userId) {
        // 验证原始数据集存在
        RagQaDatasetEntity dataset = ragQaDatasetDomainService.getDataset(ragId, userId);

        // 验证版本号唯一性
        validateVersionUniqueness(ragId, version);

        // 创建版本记录
        RagVersionEntity ragVersion = new RagVersionEntity();
        ragVersion.setName(dataset.getName());
        ragVersion.setIcon(dataset.getIcon());
        ragVersion.setDescription(dataset.getDescription());
        ragVersion.setUserId(userId);
        ragVersion.setVersion(version);
        ragVersion.setChangeLog(changeLog);
        ragVersion.setOriginalRagId(ragId);
        ragVersion.setOriginalRagName(dataset.getName());
        ragVersion.setPublishStatus(RagPublishStatus.PUBLISHED.getCode());
        ragVersionMapper.insert(ragVersion);

        // 复制文件和文档数据
        copyFilesAndDocuments(ragId, ragVersion.getId());

        // 更新统计信息
        updateVersionStatistics(ragVersion.getId());

        return ragVersion;
    }
    /** 验证版本号唯一性
     *
     * @param ragId 原始RAG数据集ID
     * @param version 版本号 */
    private void validateVersionUniqueness(String ragId, String version) {
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, ragId).eq(RagVersionEntity::getVersion, version);
        if (ragVersionMapper.exists(wrapper)) {
            throw new BusinessException("版本号已存在");
        }
    }

    /** 复制文件和文档数据到版本快照
     *
     * @param ragId 原始RAG数据集ID
     * @param ragVersionId RAG版本ID */
    private void copyFilesAndDocuments(String ragId, String ragVersionId) {
        // 获取原始文件列表
        LambdaQueryWrapper<FileDetailEntity> fileWrapper = Wrappers.<FileDetailEntity>lambdaQuery()
                .eq(FileDetailEntity::getDataSetId, ragId);
        List<FileDetailEntity> originalFiles = fileDetailMapper.selectList(fileWrapper);

        for (FileDetailEntity originalFile : originalFiles) {
            // 创建文件快照
            RagVersionFileEntity versionFile = new RagVersionFileEntity();
            versionFile.setRagVersionId(ragVersionId);
            versionFile.setOriginalFileId(originalFile.getId());
            versionFile.setFileName(originalFile.getOriginalFilename());
            versionFile.setFileSize(originalFile.getSize());
            versionFile.setFilePageSize(originalFile.getFilePageSize());
            versionFile.setFileType(originalFile.getExt());
            versionFile.setFilePath(originalFile.getPath());
            versionFile.setProcessStatus(originalFile.getIsInitialize());
            versionFile.setEmbeddingStatus(originalFile.getIsEmbedding());
            ragVersionFileMapper.insert(versionFile);

            // 复制文档单元
            copyDocumentUnits(originalFile.getId(), ragVersionId, versionFile.getId());
        }
    }

    /** 复制文档单元到版本快照
     *
     * @param originalFileId 原始文件ID
     * @param ragVersionId RAG版本ID
     * @param ragVersionFileId RAG版本文件ID */
    private void copyDocumentUnits(String originalFileId, String ragVersionId, String ragVersionFileId) {
        LambdaQueryWrapper<DocumentUnitEntity> docWrapper = Wrappers.<DocumentUnitEntity>lambdaQuery()
                .eq(DocumentUnitEntity::getFileId, originalFileId).orderByAsc(DocumentUnitEntity::getPage)
                .orderByAsc(DocumentUnitEntity::getCreatedAt);
        List<DocumentUnitEntity> documents = documentUnitMapper.selectList(docWrapper);

        for (DocumentUnitEntity doc : documents) {
            RagVersionDocumentEntity versionDoc = new RagVersionDocumentEntity();
            versionDoc.setRagVersionId(ragVersionId);
            versionDoc.setRagVersionFileId(ragVersionFileId);
            versionDoc.setOriginalDocumentId(doc.getId());
            versionDoc.setContent(doc.getContent());
            versionDoc.setPage(doc.getPage());
            // vectorId 将在向量复制时设置
            ragVersionDocumentMapper.insert(versionDoc);
        }
    }
    /** 更新版本统计信息
     *
     * @param ragVersionId RAG版本ID */
    private void updateVersionStatistics(String ragVersionId) {
        // 统计文件数量和大小
        LambdaQueryWrapper<RagVersionFileEntity> fileWrapper = Wrappers.<RagVersionFileEntity>lambdaQuery()
                .eq(RagVersionFileEntity::getRagVersionId, ragVersionId);
        List<RagVersionFileEntity> files = ragVersionFileMapper.selectList(fileWrapper);

        int fileCount = files.size();
        long totalSize = files.stream().mapToLong(RagVersionFileEntity::getFileSize).sum();

        // 统计文档数量
        LambdaQueryWrapper<RagVersionDocumentEntity> docWrapper = Wrappers.<RagVersionDocumentEntity>lambdaQuery()
                .eq(RagVersionDocumentEntity::getRagVersionId, ragVersionId);
        long documentCount = ragVersionDocumentMapper.selectCount(docWrapper);

        // 更新版本记录
        RagVersionEntity update = new RagVersionEntity();
        update.setId(ragVersionId);
        update.setFileCount(fileCount);
        update.setTotalSize(totalSize);
        update.setDocumentCount((int) documentCount);
        ragVersionMapper.updateById(update);
    }

    /** 根据原始RAG ID和版本号查找版本
     *
     * @param originalRagId 原始RAG数据集ID
     * @param version 版本号
     * @param userId 用户ID
     * @return 版本实体，如果不存在返回null */
    public RagVersionEntity findVersionByOriginalRagIdAndVersion(String originalRagId, String version, String userId) {
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, originalRagId).eq(RagVersionEntity::getVersion, version)
                .eq(RagVersionEntity::getUserId, userId);

        return ragVersionMapper.selectOne(wrapper);
    }

    /** 更新版本基本信息
     *
     * @param versionId 版本ID
     * @param name 新名称
     * @param description 新描述
     * @param icon 新图标
     * @param userId 用户ID */
    public void updateVersionBasicInfo(String versionId, String name, String description, String icon, String userId) {
        LambdaUpdateWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaUpdate()
                .eq(RagVersionEntity::getId, versionId).eq(RagVersionEntity::getUserId, userId)
                .set(RagVersionEntity::getName, name).set(RagVersionEntity::getDescription, description)
                .set(RagVersionEntity::getIcon, icon);

        ragVersionMapper.update(null, wrapper);
    }

    /** 获取RAG的版本历史
     * @param ragId 原始RAG数据集ID
     * @param userId 用户ID
     * @return 版本列表 */
    /** 获取RAG版本历史 智能权限处理： - 如果是RAG创建者：返回自己发布的所有版本（包括审核中的） - 如果不是创建者：返回该RAG的所有已发布版本（供版本切换使用）
     *
     * @param ragId 原始RAG数据集ID
     * @param userId 当前用户ID
     * @return 版本历史列表 */
    public List<RagVersionEntity> getVersionHistory(String ragId, String userId) {
        // 首先获取原始RAG信息，判断当前用户是否是创建者
        RagQaDatasetEntity originalRag = ragQaDatasetDomainService.findDatasetById(ragId);
        boolean isCreator = originalRag != null && userId.equals(originalRag.getUserId());

        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, ragId);

        if (isCreator) {
            // 创建者：查看自己发布的所有版本（包括审核中、被拒绝的）
            wrapper.eq(RagVersionEntity::getUserId, userId);
        } else {
            // 非创建者：只查看已发布的版本（供版本切换）
            wrapper.eq(RagVersionEntity::getPublishStatus, RagPublishStatus.PUBLISHED.getCode());
        }

        wrapper.orderByDesc(RagVersionEntity::getCreatedAt);
        return ragVersionMapper.selectList(wrapper);
    }

    /** 删除RAG版本
     *
     * @param versionId 版本ID
     * @param userId 用户ID */
    public void deleteRagVersion(String versionId, String userId) {
        // 验证版本是否存在且属于当前用户
        RagVersionEntity ragVersion = getRagVersion(versionId);
        if (!ragVersion.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该RAG版本");
        }

        // 删除版本文件关联
        LambdaQueryWrapper<RagVersionFileEntity> fileWrapper = Wrappers.<RagVersionFileEntity>lambdaQuery()
                .eq(RagVersionFileEntity::getRagVersionId, versionId);
        ragVersionFileMapper.delete(fileWrapper);

        // 删除版本文档关联
        LambdaQueryWrapper<RagVersionDocumentEntity> docWrapper = Wrappers.<RagVersionDocumentEntity>lambdaQuery()
                .eq(RagVersionDocumentEntity::getRagVersionId, versionId);
        ragVersionDocumentMapper.delete(docWrapper);

        // 删除版本本身
        LambdaQueryWrapper<RagVersionEntity> versionWrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getId, versionId).eq(RagVersionEntity::getUserId, userId);
        ragVersionMapper.checkedDelete(versionWrapper);
    }

    /** 分页查询已发布的RAG版本（市场）
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果 */
    public IPage<RagVersionEntity> listPublishedVersions(Integer page, Integer pageSize, String keyword) {
        // 1. 查询所有已发布的版本，支持关键词搜索
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getPublishStatus, RagPublishStatus.PUBLISHED.getCode())
                .ne(RagVersionEntity::getVersion, "0.0.1"); // 过滤掉0.0.1版本（用户私有版本）

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(RagVersionEntity::getName, keyword).or().like(RagVersionEntity::getDescription,
                    keyword));
        }

        wrapper.orderByDesc(RagVersionEntity::getPublishedAt);
        List<RagVersionEntity> allPublishedList = ragVersionMapper.selectList(wrapper);

        // 2. 按originalRagId分组，取每组publishedAt最大的一条（最新发布版本）
        java.util.Map<String, RagVersionEntity> latestMap = allPublishedList.stream()
                .collect(java.util.stream.Collectors.toMap(RagVersionEntity::getOriginalRagId, v -> v, (v1, v2) -> {
                    // 比较发布时间，选择最新的版本
                    if (v1.getPublishedAt() == null && v2.getPublishedAt() == null) {
                        return v1.getCreatedAt().isAfter(v2.getCreatedAt()) ? v1 : v2;
                    } else if (v1.getPublishedAt() == null) {
                        return v2;
                    } else if (v2.getPublishedAt() == null) {
                        return v1;
                    } else {
                        return v1.getPublishedAt().isAfter(v2.getPublishedAt()) ? v1 : v2;
                    }
                }));

        List<RagVersionEntity> latestList = new java.util.ArrayList<>(latestMap.values());

        // 3. 按发布时间倒序排列
        latestList.sort((a, b) -> {
            LocalDateTime timeA = a.getPublishedAt() != null ? a.getPublishedAt() : a.getCreatedAt();
            LocalDateTime timeB = b.getPublishedAt() != null ? b.getPublishedAt() : b.getCreatedAt();
            return timeB.compareTo(timeA);
        });

        // 4. 手动分页
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, latestList.size());
        List<RagVersionEntity> pageList = fromIndex >= latestList.size()
                ? new java.util.ArrayList<>()
                : latestList.subList(fromIndex, toIndex);

        Page<RagVersionEntity> resultPage = new Page<>(page, pageSize, latestList.size());
        resultPage.setRecords(pageList);
        return resultPage;
    }

    /** 分页查询用户的RAG版本
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果 */
    public IPage<RagVersionEntity> listUserVersions(String userId, Integer page, Integer pageSize, String keyword) {
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getUserId, userId);

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(RagVersionEntity::getName, keyword).or().like(RagVersionEntity::getDescription,
                    keyword));
        }

        wrapper.orderByDesc(RagVersionEntity::getCreatedAt);

        Page<RagVersionEntity> pageObj = new Page<>(page, pageSize);
        return ragVersionMapper.selectPage(pageObj, wrapper);
    }

    /** 获取RAG数据集的最新版本号
     *
     * @param ragId 原始RAG数据集ID
     * @param userId 用户ID
     * @return 最新版本号，如果没有版本则返回null */
    public String getLatestVersionNumber(String ragId, String userId) {
        // 查询该数据集的最新版本
        LambdaQueryWrapper<RagVersionEntity> wrapper = Wrappers.<RagVersionEntity>lambdaQuery()
                .eq(RagVersionEntity::getOriginalRagId, ragId).eq(RagVersionEntity::getUserId, userId)
                .orderByDesc(RagVersionEntity::getCreatedAt).last("limit 1");

        RagVersionEntity latestVersion = ragVersionMapper.selectOne(wrapper);

        return latestVersion != null ? latestVersion.getVersion() : null;
    }
}
