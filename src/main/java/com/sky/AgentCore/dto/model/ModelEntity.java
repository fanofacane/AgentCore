package com.sky.AgentCore.dto.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ModelTypeConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.dto.enums.ModelType;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

@Data
@TableName("models")
public class ModelEntity extends BaseEntity {

    private String id;

    private String userId;
    private String providerId;
    private String modelId;
    private String name;
    private String description;

    /** 模型部署名称 */
    private String modelEndpoint;

    private Boolean isOfficial;

    @TableField(typeHandler = ModelTypeConverter.class, jdbcType = JdbcType.VARCHAR)
    private ModelType type;

    private Boolean status;
    public void isActive() {
        if (!status) {
            throw new BusinessException("模型未激活");
        }
    }
}
