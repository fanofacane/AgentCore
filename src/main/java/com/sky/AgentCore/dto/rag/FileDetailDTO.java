package com.sky.AgentCore.dto.rag;

import lombok.Data;

import java.time.LocalDateTime;

/** 文件详情数据传输对象*/
@Data
public class FileDetailDTO {

    /** 文件ID */
    private String id;

    /** 文件访问地址 */
    private String url;

    /** 文件大小，单位字节 */
    private Long size;

    /** 文件名称 */
    private String filename;

    /** 原始文件名 */
    private String originalFilename;

    /** 文件扩展名 */
    private String ext;

    /** MIME类型 */
    private String contentType;

    /** 数据集ID */
    private String dataSetId;

    /** 总页数 */
    private Integer filePageSize;

    /** 初始化状态 */
    private Integer isInitialize;

    /** 向量化状态 */
    private Integer isEmbedding;

    /** 当前处理页数 */
    private Integer currentPageNumber;

    /** 处理进度百分比 */
    private Double processProgress;

    /** 用户ID */
    private String userId;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
