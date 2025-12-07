package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.auth.AuthConfigDTO;
import com.sky.AgentCore.dto.auth.AuthSettingEntity;
import com.sky.AgentCore.enums.AuthFeatureKey;

public interface AuthSettingAppService extends IService<AuthSettingEntity> {
    AuthConfigDTO getAuthConfig();

    boolean isFeatureEnabled(AuthFeatureKey authFeatureKey);
}
