package com.sky.AgentCore.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.UserSettingsConfigConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

/** 用户设置领域模型 */
@Data
@Component
@TableName("user_settings")
public class UserSettingsEntity extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设置配置
     */
    @TableField(typeHandler = UserSettingsConfigConverter.class, jdbcType = JdbcType.OTHER)
    private UserSettingsConfig settingConfig;

    /** 获取默认模型ID */
    public String getDefaultModelId() {
        if (settingConfig == null) {
            return null;
        }
        return settingConfig.getDefaultModel();
    }

    /** 设置默认模型ID */
    public void setDefaultModelId(String modelId) {
        if (settingConfig == null) {
            settingConfig = new UserSettingsConfig();
        }
        settingConfig.setDefaultModel(modelId);
    }
}
