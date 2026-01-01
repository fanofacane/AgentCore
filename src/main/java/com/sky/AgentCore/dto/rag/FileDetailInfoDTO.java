package com.sky.AgentCore.dto.rag;


import lombok.Data;

/** 文件详细信息DTO（包含文件路径） */
@Data
public class FileDetailInfoDTO {

    /** 文件ID */
    private String fileId;

    /** 原始文件名 */
    private String originalFilename;

    /** 文件访问地址 */
    private String url;

    /** 文件存储路径 */
    private String path;

    /** 基础存储路径 */
    private String basePath;

    /** 文件大小 */
    private Long size;

    /** 文件扩展名 */
    private String ext;

    /** 总页数 */
    private Integer filePageSize;

    /** 数据集ID */
    private String dataSetId;
}
