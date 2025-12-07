package com.sky.AgentCore.dto.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.Exceptions.BusinessException;
import lombok.Data;

import java.util.Objects;

/** 服务提供商领域模型 */
@Data
@TableName("providers")
public class ProviderEntity {

    private String id;
    private String userId;

    private String protocol;
    private String name;
    private String description;

    private String apiKey;
    private String baseUrl;

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
