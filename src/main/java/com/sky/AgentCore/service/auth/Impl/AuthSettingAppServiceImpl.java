package com.sky.AgentCore.service.auth.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.auth.AuthConfigDTO;
import com.sky.AgentCore.dto.auth.AuthSetting;
import com.sky.AgentCore.dto.auth.AuthSettingEntity;
import com.sky.AgentCore.dto.login.LoginMethodDTO;
import com.sky.AgentCore.enums.AuthFeatureKey;
import com.sky.AgentCore.enums.FeatureType;
import com.sky.AgentCore.enums.SsoProvider;
import com.sky.AgentCore.mapper.AuthMapper;
import com.sky.AgentCore.service.auth.AuthSettingAppService;
import com.sky.AgentCore.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthSettingAppServiceImpl extends ServiceImpl<AuthMapper,AuthSetting> implements AuthSettingAppService {

    /**
     * 获取前端认证配置
     *
     * @return 认证配置DTO
     */
    @Override
    public AuthConfigDTO getAuthConfig() {
        // 获取启用的登录方式
        List<AuthSetting> loginSettings = lambdaQuery().eq(AuthSetting::getFeatureType, FeatureType.LOGIN).eq(AuthSetting::getEnabled, true).orderByAsc(AuthSetting::getDisplayOrder).list();
        Map<String, LoginMethodDTO> loginMethods = new HashMap<>();
        for (AuthSetting setting : loginSettings) {
            LoginMethodDTO method = new LoginMethodDTO();
            method.setEnabled(setting.getEnabled());
            method.setName(setting.getFeatureName());

            // 根据功能键设置provider
            String providerCode = getProviderCodeByFeatureKey(setting.getFeatureKey());
            if (providerCode != null) {
                method.setProvider(providerCode);
            }

            loginMethods.put(setting.getFeatureKey(), method);
        }

        // 检查注册是否启用
        List<AuthSetting> list = lambdaQuery().eq(AuthSetting::getFeatureType, AuthFeatureKey.USER_REGISTER).eq(AuthSetting::getEnabled, true).list();
        boolean registerEnabled = list.isEmpty();
        AuthConfigDTO config = new AuthConfigDTO();
        config.setLoginMethods(loginMethods);
        config.setRegisterEnabled(registerEnabled);

        return config;
    }

    @Override
    public boolean isFeatureEnabled(AuthFeatureKey authFeatureKey) {
        return lambdaQuery().eq(AuthSetting::getFeatureKey, authFeatureKey.getCode())
                .eq(AuthSetting::getEnabled, true)
                .count() > 0;
    }


    /** 根据功能键获取认证配置
     *
     * @param featureKey 功能键
     * @return 认证配置实体 */
    @Override
    public AuthSettingEntity getByFeatureKey(AuthFeatureKey featureKey) {
        AuthSetting authSetting = lambdaQuery().eq(AuthSetting::getFeatureKey, featureKey.getCode()).one();
        Map<String, Object> stringObjectMap = JsonUtils.parseMap(authSetting.getConfigData());
        return new AuthSettingEntity(authSetting.getId(), authSetting.getFeatureType(),
                authSetting.getFeatureKey(), authSetting.getFeatureName(),
                authSetting.getEnabled(),stringObjectMap, authSetting.getDisplayOrder(),
                authSetting.getDescription());
    }

    /**
     * 根据认证功能键获取对应的SSO提供商代码
     *
     * @param featureKey 认证功能键
     * @return SSO提供商代码（大写）
     */
    private String getProviderCodeByFeatureKey(String featureKey) {
        if (AuthFeatureKey.GITHUB_LOGIN.getCode().equals(featureKey)) {
            return SsoProvider.GITHUB.getCode().toUpperCase();
        } else if (AuthFeatureKey.COMMUNITY_LOGIN.getCode().equals(featureKey)) {
            return SsoProvider.COMMUNITY.getCode().toUpperCase();
        }
        return null;
    }
}
