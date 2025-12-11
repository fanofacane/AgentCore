package com.sky.AgentCore.service.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.user.UserSettingsDTO;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.dto.user.UserSettingsUpdateRequest;

import java.util.List;

public interface UserSettingsDomainService extends IService<UserSettingsEntity> {
    /** 获取用户默认模型ID
     * @param userId 用户ID
     * @return 默认模型ID */
    String getUserDefaultModelId(String userId);

    UserSettingsDTO getUserSettings(String userId);

    UserSettingsDTO updateUserSettings(UserSettingsUpdateRequest request, String userId);

    List<String> getUserFallbackChain(String userId);

    void setUserDefaultModelId(String userId, String id);
}
