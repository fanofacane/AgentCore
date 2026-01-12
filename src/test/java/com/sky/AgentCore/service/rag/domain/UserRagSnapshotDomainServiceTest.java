package com.sky.AgentCore.service.rag.domain;

import com.sky.AgentCore.dto.rag.RagVersionFileEntity;
import com.sky.AgentCore.dto.rag.UserRagFileEntity;
import com.sky.AgentCore.mapper.rag.RagVersionDocumentMapper;
import com.sky.AgentCore.mapper.rag.RagVersionFileMapper;
import com.sky.AgentCore.mapper.rag.UserRagDocumentMapper;
import com.sky.AgentCore.mapper.rag.UserRagFileMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRagSnapshotDomainServiceTest {

    @Mock
    private UserRagFileMapper userRagFileMapper;
    @Mock
    private UserRagDocumentMapper userRagDocumentMapper;
    @Mock
    private RagVersionFileMapper ragVersionFileMapper;
    @Mock
    private RagVersionDocumentMapper ragVersionDocumentMapper;

    @InjectMocks
    private UserRagSnapshotDomainService userRagSnapshotDomainService;

    @Test
    void copyVersionFilesToUser_shouldNotReuseVersionFileId() {
        RagVersionFileEntity versionFile = new RagVersionFileEntity();
        versionFile.setId("version-file-id");
        versionFile.setOriginalFileId("original-file-id");
        versionFile.setFileName("a.pdf");
        versionFile.setCreatedAt(LocalDateTime.now().minusDays(1));
        versionFile.setUpdatedAt(LocalDateTime.now().minusDays(1));

        when(ragVersionFileMapper.selectList(any())).thenReturn(List.of(versionFile));

        userRagSnapshotDomainService.copyVersionFilesToUser("user-rag-id", "rag-version-id");

        ArgumentCaptor<UserRagFileEntity> captor = ArgumentCaptor.forClass(UserRagFileEntity.class);
        verify(userRagFileMapper, times(1)).insert(captor.capture());

        UserRagFileEntity inserted = captor.getValue();
        assertThat(inserted.getUserRagId()).isEqualTo("user-rag-id");
        assertThat(inserted.getId()).isNull();
        assertThat(inserted.getCreatedAt()).isNull();
        assertThat(inserted.getUpdatedAt()).isNull();
        assertThat(inserted.getDeletedAt()).isNull();
        assertThat(inserted.getOriginalFileId()).isEqualTo("original-file-id");
        assertThat(inserted.getFileName()).isEqualTo("a.pdf");
    }
}

