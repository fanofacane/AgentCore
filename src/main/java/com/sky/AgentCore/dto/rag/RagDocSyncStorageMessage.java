package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.model.ModelConfig;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class RagDocSyncStorageMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = -5764144581856293209L;
    /** 主键 */
    private String id;

    /** 文件ID */
    private String fileId;

    /** 页码 */
    private Integer page;

    /** 当前页内容 */
    private String content;

    /** 是否进行向量化 */
    private Boolean isVector;

    private String fileName;

    /** 数据集ID */
    private String datasetId;

    /** 用户ID */
    private String userId;

    /** 嵌入模型配置 */
    private ModelConfig embeddingModelConfig;

}
