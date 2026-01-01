package com.sky.AgentCore.dto.rag;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RagVersionDTO {

    /** 版本ID */
    private String id;

    /** 快照时的名称 */
    private String name;

    /** 快照时的图标 */
    private String icon;

    /** 快照时的描述 */
    private String description;

    /** 创建者ID */
    private String userId;

    /** 创建者昵称 */
    private String userNickname;

    /** 版本号 */
    private String version;

    /** 更新日志 */
    private String changeLog;

    /** 标签列表 */
    private List<String> labels;

    /** 原始RAG数据集ID */
    private String originalRagId;

    /** 原始RAG名称 */
    private String originalRagName;

    /** 文件数量 */
    private Integer fileCount;

    /** 总大小（字节） */
    private Long totalSize;

    /** 文档单元数量 */
    private Integer documentCount;

    /** 发布状态：1:审核中, 2:已发布, 3:拒绝, 4:已下架 */
    private Integer publishStatus;

    /** 发布状态描述 */
    private String publishStatusDesc;

    /** 审核拒绝原因 */
    private String rejectReason;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 安装次数 */
    private Long installCount;

    /** 是否已安装（当前用户） */
    private Boolean isInstalled;

}
