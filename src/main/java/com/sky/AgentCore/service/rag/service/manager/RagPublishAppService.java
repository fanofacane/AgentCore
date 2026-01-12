package com.sky.AgentCore.service.rag.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.converter.assembler.RagVersionAssembler;
import com.sky.AgentCore.dto.rag.PublishRagRequest;
import com.sky.AgentCore.dto.rag.QueryUserRagVersionRequest;
import com.sky.AgentCore.dto.rag.RagVersionDTO;
import com.sky.AgentCore.dto.rag.RagVersionEntity;
import com.sky.AgentCore.service.rag.domain.RagVersionDomainService;
import com.sky.AgentCore.service.rag.domain.UserRagDomainService;
import com.sky.AgentCore.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RagPublishAppService {
    @Autowired
    private UserRagDomainService userRagDomainService;
    @Autowired
    private RagVersionDomainService ragVersionDomainService;
    @Autowired
    private UserService userService;
    /** 发布RAG版本
     *
     * @param request 发布请求
     * @param userId 用户ID
     * @return 发布的版本信息 */
    @Transactional
    public RagVersionDTO publishRagVersion(PublishRagRequest request, String userId) {

        // 创建版本快照
        RagVersionEntity ragVersion = ragVersionDomainService.createRagVersionSnapshot(request.getRagId(),
                request.getVersion(), request.getChangeLog(), userId);

        userRagDomainService.installRag(userId, ragVersion.getId());

        // 转换为DTO
        RagVersionDTO dto = RagVersionAssembler.toDTO(ragVersion);

        // 设置用户信息
        enrichWithUserInfo(dto);

        dto.setIsInstalled(true);
        return dto;
    }

    /** 丰富用户信息
     *
     * @param dto RAG版本DTO */
    private void enrichWithUserInfo(RagVersionDTO dto) {
        if (dto == null || StringUtils.isBlank(dto.getUserId())) return;

        try {
            var user = userService.getUserInfo(dto.getUserId());
            if (user != null) dto.setUserNickname(user.getNickname());

        } catch (Exception e) {
            // 忽略用户查询异常
        }
    }

    /** 获取RAG的版本历史
     *
     * @param ragId 原始RAG数据集ID
     * @param userId 用户ID
     * @return 版本历史列表 */
    public List<RagVersionDTO> getRagVersionHistory(String ragId, String userId) {
        List<RagVersionEntity> versions = ragVersionDomainService.getVersionHistory(ragId, userId);

        // 转换为DTO
        List<RagVersionDTO> dtoList = RagVersionAssembler.toDTOs(versions);

        // 设置用户信息
        for (RagVersionDTO dto : dtoList) {
            enrichWithUserInfo(dto);
        }

        return dtoList;
    }

    /** 获取用户的RAG版本列表
     *
     * @param userId 用户ID
     * @param request 查询请求
     * @return 版本列表 */
    public Page<RagVersionDTO> getUserRagVersions(String userId, QueryUserRagVersionRequest request) {
        IPage<RagVersionEntity> entityPage = ragVersionDomainService.listUserVersions(userId, request.getPage(),
                request.getPageSize(), request.getKeyword());

        // 转换为DTO
        List<RagVersionDTO> dtoList = RagVersionAssembler.toDTOs(entityPage.getRecords());

        // 设置用户信息和安装次数
        for (RagVersionDTO dto : dtoList) {
            enrichWithUserInfo(dto);
            dto.setInstallCount(userRagDomainService.getInstallCount(dto.getId()));
        }

        // 创建DTO分页对象
        Page<RagVersionDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /** 获取RAG版本详情
     *
     * @param versionId 版本ID
     * @param currentUserId 当前用户ID（用于判断是否已安装）
     * @return 版本详情 */
    public RagVersionDTO getRagVersionDetail(String versionId, String currentUserId) {
        RagVersionEntity version = ragVersionDomainService.getRagVersion(versionId);

        // 转换为DTO
        RagVersionDTO dto = RagVersionAssembler.toDTO(version);

        // 设置用户信息
        enrichWithUserInfo(dto);

        // 设置安装次数
        dto.setInstallCount(userRagDomainService.getInstallCount(versionId));

        // 设置是否已安装
        if (StringUtils.isNotBlank(currentUserId)) {
            boolean isInstalled = false;
            if (version != null && currentUserId.equals(version.getUserId())) {
                isInstalled = true;
            } else if (version != null && StringUtils.isNotBlank(version.getOriginalRagId())) {
                isInstalled = userRagDomainService.isRagInstalledByOriginalId(currentUserId, version.getOriginalRagId());
            } else {
                isInstalled = userRagDomainService.isInstalledOrOwned(currentUserId, versionId);
            }
            dto.setIsInstalled(isInstalled);
        }

        return dto;
    }

    /** 获取RAG数据集的最新版本号
     *
     * @param ragId 原始RAG数据集ID
     * @param userId 用户ID
     * @return 最新版本号，如果没有版本则返回null */
    public String getLatestVersionNumber(String ragId, String userId) {
        return ragVersionDomainService.getLatestVersionNumber(ragId, userId);
    }

}
