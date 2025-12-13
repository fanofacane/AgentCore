package com.sky.AgentCore.dto.session;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 会话DTO，用于API响应 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionDTO {
    /** 会话ID */
    private String id;
    /** 会话标题 */
    private String title;
    /** 会话描述 */
    private String description;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 是否归档 */
    private boolean isArchived;
    /** 代理ID */
    private String agentId;

    /** 支持多模态 */
    private Boolean multiModal;
}
