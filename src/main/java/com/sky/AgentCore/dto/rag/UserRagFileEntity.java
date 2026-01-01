package com.sky.AgentCore.dto.rag;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 用户RAG文件快照实体
 * @author fanofacane
 */
@Data
@TableName("user_rag_files")
public class UserRagFileEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /** 文件快照ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 关联的用户RAG ID */
    private String userRagId;

    /** 原始文件ID（仅标识） */
    private String originalFileId;

    /** 文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件页数 */
    private Integer filePageSize;

    /** 文件类型 */
    private String fileType;

    /** 文件存储路径 */
    private String filePath;

    /** 处理状态 */
    private Integer processStatus;

    /** 向量化状态 */
    private Integer embeddingStatus;
}
