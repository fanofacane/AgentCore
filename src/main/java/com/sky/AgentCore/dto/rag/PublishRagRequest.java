package com.sky.AgentCore.dto.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
@Data
public class PublishRagRequest {

    /** 原始RAG数据集ID */
    @NotBlank(message = "RAG数据集ID不能为空")
    private String ragId;

    /** 版本号 */
    @NotBlank(message = "版本号不能为空")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "版本号格式错误，应为x.x.x格式")
    private String version;

    /** 更新日志 */
    private String changeLog;

    /** 标签列表 */
    private List<String> labels;
}
