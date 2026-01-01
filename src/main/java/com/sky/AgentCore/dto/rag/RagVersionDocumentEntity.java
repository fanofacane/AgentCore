package com.sky.AgentCore.dto.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** RAG版本文档单元实体（文档内容快照）
 * @author fanofacane
 */
@Data
@TableName("rag_version_documents")
public class RagVersionDocumentEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /** 文档单元ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 关联的RAG版本ID */
    private String ragVersionId;

    /** 关联的版本文件ID */
    private String ragVersionFileId;

    /** 原始文档单元ID（仅标识） */
    private String originalDocumentId;

    /** 文档内容 */
    private String content;

    /** 页码 */
    private Integer page;

    /** 向量ID（在向量数据库中的ID） */
    private String vectorId;
}
