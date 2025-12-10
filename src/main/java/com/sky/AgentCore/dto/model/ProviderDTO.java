package com.sky.AgentCore.dto.model;


import com.sky.AgentCore.enums.ProviderProtocol;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** 服务提供商DTO */
@Data
public class ProviderDTO {

    /** 服务商id */
    private String id;
    /** 服务商协议 */
    private ProviderProtocol protocol;
    /** 服务商名称 */
    private String name;
    /** 服务商描述 */
    private String description;
    /** 服务商配置 */
    private ProviderConfig config;
    /** 是否官方 */
    private Boolean isOfficial;
    /** 服务商状态 */
    private Boolean status;
    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
    /** 模型列表 */
    private List<ModelDTO> models = new ArrayList<>();

    /** 脱敏配置信息（用于返回前端） */
    public void maskSensitiveInfo() {
        if (this.config != null) {
            // 如果有API Key，则脱敏处理
            if (this.config.getApiKey() != null && !this.config.getApiKey().isEmpty()) {
                this.config.setApiKey("***********");
            }
        }
    }
}