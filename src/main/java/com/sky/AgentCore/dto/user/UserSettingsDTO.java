package com.sky.AgentCore.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 用户设置数据传输对象 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsDTO {
    /** 主键ID */
    private String id;

    /** 用户ID */
    private String userId;

    /** 配置 */
    private UserSettingsConfig settingConfig;

    public UserSettingsDTO(String userId, UserSettingsConfig settingConfig) {
        this.userId = userId;
        this.settingConfig = settingConfig;
    }
}
