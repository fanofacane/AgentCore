package com.sky.AgentCore.dto.auth;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.MapConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.util.Map;

/** 认证配置实体类 */
@TableName("auth_settings")
@Data
public class AuthSettingEntity extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("feature_type")
    private String featureType;

    @TableField("feature_key")
    private String featureKey;

    @TableField("feature_name")
    private String featureName;

    @TableField("enabled")
    private Boolean enabled;

    @TableField(value = "config_data")
    private Map<String, Object> configData;

    @TableField("display_order")
    private Integer displayOrder;

    @TableField("description")
    private String description;

    public AuthSettingEntity(String id, String featureType, String featureKey, String featureName, Boolean enabled, Map<String, Object> configData, Integer displayOrder, String description) {
        this.id = id;
        this.featureType = featureType;
        this.featureKey = featureKey;
        this.featureName = featureName;
        this.enabled = enabled;
        this.configData = configData;
        this.displayOrder = displayOrder;
        this.description = description;
    }
    public AuthSettingEntity() {
    }
}
