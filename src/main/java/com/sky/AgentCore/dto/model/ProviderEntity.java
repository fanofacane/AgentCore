package com.sky.AgentCore.dto.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ProviderConfigConverter;
import com.sky.AgentCore.converter.ProviderProtocolConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.ProviderProtocol;
import lombok.Data;

import java.util.Objects;

/** 服务提供商领域模型 */
@Data
@TableName("providers")
public class ProviderEntity extends BaseEntity {

    private String id;
    private String userId;

    @TableField(typeHandler = ProviderProtocolConverter.class)
    private ProviderProtocol protocol;

    private String name;
    private String description;

    @TableField(typeHandler = ProviderConfigConverter.class)
    private ProviderConfig config;

    private Boolean isOfficial;
    private Boolean status;
    public void isActive() {
        if (!status) {
            throw new BusinessException("服务商未激活");
        }
    }

    public void isAvailable(String userId) {
        if (!isOfficial && !Objects.equals(this.getUserId(), userId)) {
            throw new BusinessException("模型未找到");
        }
    }
}
