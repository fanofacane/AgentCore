package com.sky.AgentCore.dto.rag;


import lombok.Data;

import java.time.LocalDateTime;

/** RAG知识库数据集数据传输对象 */
@Data
public class RagQaDatasetDTO {

    /**
     * 数据集ID
     */
    private String id;

    /**
     * 用户RAG安装记录ID（用于调用已安装RAG相关接口）
     */
    private String userRagId;

    /**
     * 数据集名称
     */
    private String name;

    /**
     * 数据集图标
     */
    private String icon;

    /**
     * 数据集说明
     */
    private String description;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件数量
     */
    private Long fileCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
