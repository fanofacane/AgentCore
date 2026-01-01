package com.sky.AgentCore.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InstallRagRequest {
    /** RAG版本ID */
    @NotBlank(message = "RAG版本ID不能为空")
    private String ragVersionId;
}
