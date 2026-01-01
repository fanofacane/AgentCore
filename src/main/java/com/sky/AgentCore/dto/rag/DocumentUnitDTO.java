package com.sky.AgentCore.dto.rag;


import lombok.Data;

/** 文档单元响应DTO */
@Data
public class DocumentUnitDTO {

    /** 主键 */
    private String id;

    /** 文件ID */
    private String fileId;

    /** 页码 */
    private Integer page;

    /** 内容 */
    private String content;

    /** 是否OCR处理 */
    private Boolean isOcr;

    /** 是否向量化 */
    private Boolean isVector;

    /** 创建时间 */
    private String createdAt;

    /** 更新时间 */
    private String updatedAt;
    @Override
    public String toString() {
        return content;

    }
}
