package com.sky.AgentCore.dto.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

/** 用户设置领域模型 */
@Data
@TableName("user_settings")
public class UserSettingsEntity extends BaseEntity {

    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设置配置
     */
    private String settingConfig;
    /** 获取默认模型ID */
/*    public String getDefaultModelId() {
        if (settingConfig == null) {
            return null;
        }
        return settingConfig.getDefaultModel();
    }*/

    /** 设置默认模型ID */
/*    public void setDefaultModelId(String modelId) {
        if (settingConfig == null) {
            settingConfig = new UserSettingsConfig();
        }
        settingConfig.setDefaultModel(modelId);
    }*/
}
