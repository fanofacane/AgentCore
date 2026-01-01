package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.model.ModelConfig;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class RagDocMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 5517731583403276913L;

    /** 文件id */
    private String fileId;

    /** 文件总页数 */
    private Integer pageSize;

    /** 用户ID */
    private String userId;

    /** OCR模型配置 */
    private ModelConfig ocrModelConfig;
}
