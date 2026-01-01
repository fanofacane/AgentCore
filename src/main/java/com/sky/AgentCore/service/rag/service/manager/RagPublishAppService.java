package com.sky.AgentCore.service.rag.service.manager;

import com.sky.AgentCore.converter.assembler.RagVersionAssembler;
import com.sky.AgentCore.dto.rag.PublishRagRequest;
import com.sky.AgentCore.dto.rag.RagVersionDTO;
import com.sky.AgentCore.dto.rag.RagVersionEntity;
import com.sky.AgentCore.service.rag.domain.RagVersionDomainService;
import com.sky.AgentCore.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RagPublishAppService {
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

        // 转换为DTO
        RagVersionDTO dto = RagVersionAssembler.toDTO(ragVersion);

        // 设置用户信息
        enrichWithUserInfo(dto);

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
}
