package com.sky.AgentCore.dto.rag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** 上传文件到数据集请求*/
@Data
public class UploadFileRequest {

    /** 数据集ID */
    @NotBlank(message = "数据集ID不能为空")
    private String datasetId;

    /** 上传的文件 */
    private MultipartFile file;
}
