package com.sky.AgentCore.service.rag.domain;

import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.rag.DocumentUnitEntity;
import com.sky.AgentCore.dto.rag.FileDetailEntity;
import com.sky.AgentCore.dto.rag.UserRagDocumentEntity;
import com.sky.AgentCore.dto.rag.UserRagEntity;
import com.sky.AgentCore.dto.rag.UserRagFileEntity;
import com.sky.AgentCore.enums.InstallType;
import com.sky.AgentCore.mapper.rag.DocumentUnitMapper;
import com.sky.AgentCore.mapper.rag.FileDetailMapper;
import com.sky.AgentCore.mapper.rag.UserRagDocumentMapper;
import com.sky.AgentCore.mapper.rag.UserRagFileMapper;
import com.sky.AgentCore.mapper.rag.UserRagMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagDataAccessDomainServiceTest {

    @Mock
    private UserRagMapper userRagMapper;
    @Mock
    private FileDetailMapper fileDetailMapper;
    @Mock
    private DocumentUnitMapper documentUnitRepository;
    @Mock
    private UserRagFileMapper userRagFileMapper;
    @Mock
    private UserRagDocumentMapper userRagDocumentMapper;

    @InjectMocks
    private RagDataAccessDomainService ragDataAccessDomainService;

    @Test
    void getRagFileInfo_snapshot_shouldSupportOriginalFileId() {
        UserRagEntity userRag = new UserRagEntity();
        userRag.setId("user-rag-id");
        userRag.setUserId("user-id");
        userRag.setInstallType(InstallType.SNAPSHOT);

        when(userRagMapper.selectOne(any())).thenReturn(userRag);

        UserRagFileEntity userFile = new UserRagFileEntity();
        userFile.setId("user-file-id");
        userFile.setUserRagId("user-rag-id");
        userFile.setOriginalFileId("original-file-id");
        userFile.setFileName("a.pdf");
        userFile.setFileSize(10L);
        userFile.setFileType("pdf");
        userFile.setFilePath("/tmp/a.pdf");
        userFile.setProcessStatus(1);
        userFile.setCreatedAt(LocalDateTime.now().minusDays(1));
        userFile.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(userRagFileMapper.selectOne(any())).thenReturn(null, userFile);

        UserRagDocumentEntity lastPageDoc = new UserRagDocumentEntity();
        lastPageDoc.setUserRagFileId("user-file-id");
        lastPageDoc.setPage(3);
        when(userRagDocumentMapper.selectList(any())).thenReturn(List.of(lastPageDoc));

        FileDetailEntity file = ragDataAccessDomainService.getRagFileInfo("user-id", "user-rag-id", "original-file-id");
        assertThat(file.getId()).isEqualTo("user-file-id");
        assertThat(file.getOriginalFilename()).isEqualTo("a.pdf");
        assertThat(file.getFilePageSize()).isEqualTo(4);
    }

    @Test
    void getRagDocumentsByFile_snapshot_shouldSupportOriginalFileId() {
        UserRagEntity userRag = new UserRagEntity();
        userRag.setId("user-rag-id");
        userRag.setUserId("user-id");
        userRag.setInstallType(InstallType.SNAPSHOT);

        when(userRagMapper.selectOne(any())).thenReturn(userRag);

        UserRagFileEntity userFile = new UserRagFileEntity();
        userFile.setId("user-file-id");
        userFile.setUserRagId("user-rag-id");
        userFile.setOriginalFileId("original-file-id");
        when(userRagFileMapper.selectOne(any())).thenReturn(null, userFile);

        UserRagDocumentEntity userDoc = new UserRagDocumentEntity();
        userDoc.setId("doc-id");
        userDoc.setUserRagId("user-rag-id");
        userDoc.setUserRagFileId("user-file-id");
        userDoc.setContent("hello");
        userDoc.setPage(1);
        userDoc.setCreatedAt(LocalDateTime.now().minusDays(1));
        userDoc.setUpdatedAt(LocalDateTime.now().minusDays(1));
        when(userRagDocumentMapper.selectList(any())).thenReturn(List.of(userDoc));

        List<DocumentUnitEntity> docs = ragDataAccessDomainService.getRagDocumentsByFile("user-id", "user-rag-id",
                "original-file-id");
        assertThat(docs).hasSize(1);
        assertThat(docs.get(0).getId()).isEqualTo("doc-id");
        assertThat(docs.get(0).getFileId()).isEqualTo("user-file-id");
        assertThat(docs.get(0).getContent()).isEqualTo("hello");
        assertThat(docs.get(0).getPage()).isEqualTo(1);
    }

    @Test
    void getRagFileInfo_snapshot_shouldThrowWhenFileNotFound() {
        UserRagEntity userRag = new UserRagEntity();
        userRag.setId("user-rag-id");
        userRag.setUserId("user-id");
        userRag.setInstallType(InstallType.SNAPSHOT);

        when(userRagMapper.selectOne(any())).thenReturn(userRag);
        when(userRagFileMapper.selectOne(any())).thenReturn(null, null);

        assertThatThrownBy(() -> ragDataAccessDomainService.getRagFileInfo("user-id", "user-rag-id", "missing-file-id"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("文件不存在或无权限访问");
    }
}

