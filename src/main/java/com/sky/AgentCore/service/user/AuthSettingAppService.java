package com.sky.AgentCore.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.auth.AuthConfigDTO;
import com.sky.AgentCore.dto.auth.AuthSetting;
import com.sky.AgentCore.dto.auth.AuthSettingEntity;
import com.sky.AgentCore.dto.enums.AuthFeatureKey;

public interface AuthSettingAppService extends IService<AuthSetting> {
    AuthConfigDTO getAuthConfig();

    boolean isFeatureEnabled(AuthFeatureKey authFeatureKey);

    AuthSettingEntity getByFeatureKey(AuthFeatureKey authFeatureKey);
}
