package com.sky.AgentCore.dto.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.Exceptions.BusinessException;
import lombok.Data;
@Data
@TableName("models")
public class ModelEntity {

    private String id;

    private String userId;
    private String providerId;
    private String modelId;
    private String name;
    private String description;

    /** 模型部署名称 */
    private String modelEndpoint;

    private Boolean isOfficial;

    private Boolean status;
    public void isActive() {
        if (!status) {
            throw new BusinessException("模型未激活");
        }
    }
}
