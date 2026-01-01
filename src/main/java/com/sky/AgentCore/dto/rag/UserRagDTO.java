package com.sky.AgentCore.dto.rag;


import com.sky.AgentCore.enums.InstallType;
import lombok.Data;

import java.time.LocalDateTime;

/** 用户安装的RAG DTO */
@Data
public class UserRagDTO {

    /** 安装记录ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** RAG版本ID */
    private String ragVersionId;

    /** 安装时的名称 */
    private String name;

    /** 安装时的描述 */
    private String description;

    /** 安装时的图标 */
    private String icon;

    /** 版本号 */
    private String version;

    /** 安装时间 */
    private LocalDateTime installedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 原始RAG ID */
    private String originalRagId;

    /** 文件数量 */
    private Integer fileCount;

    /** 文档单元数量 */
    private Integer documentCount;

    /** 创建者昵称 */
    private String creatorNickname;

    /** 创建者ID */
    private String creatorId;

    /** 安装类型 */
    private InstallType installType;
}
