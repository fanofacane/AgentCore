package com.sky.AgentCore.dto.apiKey;

import lombok.Data;

import java.time.LocalDateTime;

/** API密钥DTO */
@Data
public class ApiKeyDTO {

    /** API Key ID */
    private String id;

    /** API密钥 */
    private String apiKey;

    /** 关联的Agent ID */
    private String agentId;

    /** 关联的Agent名称 */
    private String agentName;

    /** 创建者用户ID */
    private String userId;

    /** API Key名称/描述 */
    private String name;

    /** 状态：TRUE-启用，FALSE-禁用 */
    private Boolean status;

    /** 已使用次数 */
    private Integer usageCount;

    /** 最后使用时间 */
    private LocalDateTime lastUsedAt;

    /** 过期时间 */
    private LocalDateTime expiresAt;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 是否已过期 */
    private Boolean expired;

    /** 是否可用 */
    private Boolean available;

}