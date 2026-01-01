package com.sky.AgentCore.dto.rag;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** RAG市场DTO（用于市场展示）
 * @author fanofacane
 */
@Data
public class RagMarketDTO {

    /** 版本ID */
    private String id;

    /** RAG名称 */
    private String name;

    /** RAG图标 */
    private String icon;

    /** RAG描述 */
    private String description;

    /** 版本号 */
    private String version;

    /** 标签列表 */
    private List<String> labels;

    /** 创建者ID */
    private String userId;

    /** 创建者昵称 */
    private String userNickname;

    /** 创建者头像 */
    private String userAvatar;

    /** 文件数量 */
    private Integer fileCount;

    /** 文档单元数量 */
    private Integer documentCount;

    /** 总大小（字节） */
    private Long totalSize;

    /** 总大小（格式化显示） */
    private String totalSizeDisplay;

    /** 安装次数 */
    private Long installCount;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 是否已安装（当前用户） */
    private Boolean isInstalled;

    /** 评分（预留） */
    private Double rating;

    /** 评价数量（预留） */
    private Integer reviewCount;

}
