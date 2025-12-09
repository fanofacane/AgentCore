package com.sky.AgentCore.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.user.UserSettingsDTO;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.dto.user.UserSettingsUpdateRequest;

public interface UserSettingsDomainService extends IService<UserSettingsEntity> {
    /** 获取用户默认模型ID
     * @param userId 用户ID
     * @return 默认模型ID */
    String getUserDefaultModelId(String userId);

    UserSettingsDTO getUserSettings(String userId);

    UserSettingsDTO updateUserSettings(UserSettingsUpdateRequest request, String userId);
}
