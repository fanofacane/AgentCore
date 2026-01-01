package com.sky.AgentCore.service.rag.service.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.converter.assembler.DocumentUnitAssembler;
import com.sky.AgentCore.converter.assembler.FileDetailInfoAssembler;
import com.sky.AgentCore.dto.mq.MessageEnvelope;
import com.sky.AgentCore.dto.mq.MessagePublisher;
import com.sky.AgentCore.dto.mq.events.RagDocSyncStorageEvent;
import com.sky.AgentCore.dto.rag.*;
import com.sky.AgentCore.enums.EventType;
import com.sky.AgentCore.service.rag.domain.DocumentUnitDomainService;
import com.sky.AgentCore.service.rag.domain.FileDetailDomainService;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FileOperationAppService {
    @Autowired
    private FileDetailDomainService fileDetailDomainService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DocumentUnitDomainService documentUnitDomainService;
    @Autowired
    private MessagePublisher messagePublisher;
    /** 根据文件ID获取文件详细信息
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 文件详细信息 */
    public FileDetailInfoDTO getFileDetailInfo(String fileId, String userId) {
        FileDetailEntity entity = fileDetailDomainService.getFileById(fileId, userId);
        return FileDetailInfoAssembler.toDTO(entity);
    }

    /** 批量删除文件
     *
     * @param request 批量删除请求
     * @param userId 用户ID */
    @Transactional
    public void batchDeleteFiles(BatchDeleteFilesRequest request, String userId) {
        for (String fileUrl : request.getFileUrls()) {
            try {
                fileStorageService.delete(fileUrl);
            } catch (Exception e) {
                // 记录日志但继续删除其他文件
                System.err.println("删除文件失败: " + fileUrl + ", 错误: " + e.getMessage());
            }
        }
    }

    /** 分页查询文件的语料
     *
     * @param request 查询请求
     * @param userId 用户ID
     * @return 分页结果 */
    public Page<DocumentUnitDTO> listDocumentUnits(QueryDocumentUnitsRequest request, String userId) {
        // 验证文件是否存在和权限
        fileDetailDomainService.getFileById(request.getFileId(), userId);

        IPage<DocumentUnitEntity> entityPage = documentUnitDomainService.listDocumentUnits(request.getFileId(), userId,
                request.getPage(), request.getPageSize(), request.getKeyword());

        Page<DocumentUnitDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(),
                entityPage.getTotal());

        List<DocumentUnitDTO> dtoList = DocumentUnitAssembler.toDTOs(entityPage.getRecords());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    /** 更新语料内容
     *
     * @param request 更新请求
     * @param userId 用户ID
     * @return 更新后的语料 */
    @Transactional
    public DocumentUnitDTO updateDocumentUnit(UpdateDocumentUnitRequest request, String userId) {
        // 验证语料是否存在
        DocumentUnitEntity existingEntity = documentUnitDomainService.getDocumentUnit(request.getDocumentUnitId(),
                userId);

        // 转换并更新
        DocumentUnitEntity updateEntity = DocumentUnitAssembler.toEntity(request, userId);
        documentUnitDomainService.updateDocumentUnit(updateEntity, userId);

        // 如果需要重新向量化，发送MQ消息
        if (Boolean.TRUE.equals(request.getReEmbedding())) {
            triggerReEmbedding(existingEntity, request.getContent());
        }

        // 返回更新后的实体
        DocumentUnitEntity updatedEntity = documentUnitDomainService.getDocumentUnit(request.getDocumentUnitId(),
                userId);
        return DocumentUnitAssembler.toDTO(updatedEntity);
    }

    /** 触发重新向量化
     *
     * @param documentUnit 文档单元
     * @param newContent 新内容 */
    private void triggerReEmbedding(DocumentUnitEntity documentUnit, String newContent) {
        try {
            // 获取文件信息
            FileDetailEntity fileEntity = fileDetailDomainService.getFileByIdWithoutUserCheck(documentUnit.getFileId());

            // 构建向量化消息
            RagDocSyncStorageMessage storageMessage = new RagDocSyncStorageMessage();
            storageMessage.setId(documentUnit.getId());
            storageMessage.setFileId(documentUnit.getFileId());
            storageMessage.setFileName(fileEntity.getOriginalFilename());
            storageMessage.setPage(documentUnit.getPage());
            storageMessage.setContent(newContent);
            storageMessage.setIsVector(true);
            storageMessage.setDatasetId(fileEntity.getDataSetId());

            // 发送消息
            MessageEnvelope<RagDocSyncStorageMessage> envelope = MessageEnvelope.builder(storageMessage)
                    .addEventType(EventType.DOC_SYNC_RAG).description("语料内容修改后重新向量化 - 页面 " + documentUnit.getPage())
                    .build();
            messagePublisher.publish(RagDocSyncStorageEvent.route(), envelope);

        } catch (Exception e) {
            // 记录日志但不影响主流程
            // 可以考虑使用日志框架记录错误
            System.err.println("触发重新向量化失败: " + e.getMessage());
        }
    }

    /** 删除语料
     *
     * @param documentUnitId 语料ID
     * @param userId 用户ID */
    @Transactional
    public void deleteDocumentUnit(String documentUnitId, String userId) {
        // 验证语料是否存在
        documentUnitDomainService.checkDocumentUnitExists(documentUnitId, userId);

        // 删除语料
        documentUnitDomainService.deleteDocumentUnit(documentUnitId, userId);
    }

    /** 根据语料ID获取单个语料详情
     *
     * @param documentUnitId 语料ID
     * @param userId 用户ID
     * @return 语料详情 */
    public DocumentUnitDTO getDocumentUnit(String documentUnitId, String userId) {
        DocumentUnitEntity entity = documentUnitDomainService.getDocumentUnit(documentUnitId, userId);
        return DocumentUnitAssembler.toDTO(entity);
    }
}
