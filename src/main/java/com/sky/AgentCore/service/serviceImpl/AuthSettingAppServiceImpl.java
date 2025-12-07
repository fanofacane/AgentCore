package com.sky.AgentCore.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.auth.AuthConfigDTO;
import com.sky.AgentCore.dto.auth.AuthSettingEntity;
import com.sky.AgentCore.dto.login.LoginMethodDTO;
import com.sky.AgentCore.enums.AuthFeatureKey;
import com.sky.AgentCore.enums.FeatureType;
import com.sky.AgentCore.enums.SsoProvider;
import com.sky.AgentCore.mapper.AuthMapper;
import com.sky.AgentCore.service.AuthSettingAppService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthSettingAppServiceImpl extends ServiceImpl<AuthMapper,AuthSettingEntity> implements AuthSettingAppService {

    /**
     * 获取前端认证配置
     *
     * @return 认证配置DTO
     */
    @Override
    public AuthConfigDTO getAuthConfig() {
        // 获取启用的登录方式
        List<AuthSettingEntity> loginSettings = lambdaQuery().eq(AuthSettingEntity::getFeatureType, FeatureType.LOGIN).eq(AuthSettingEntity::getEnabled, true).orderByAsc(AuthSettingEntity::getDisplayOrder).list();
        Map<String, LoginMethodDTO> loginMethods = new HashMap<>();
        for (AuthSettingEntity setting : loginSettings) {
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
        List<AuthSettingEntity> list = lambdaQuery().eq(AuthSettingEntity::getFeatureType, AuthFeatureKey.USER_REGISTER).eq(AuthSettingEntity::getEnabled, true).list();
        boolean registerEnabled = list.isEmpty();
        AuthConfigDTO config = new AuthConfigDTO();
        config.setLoginMethods(loginMethods);
        config.setRegisterEnabled(registerEnabled);

        return config;
    }

    @Override
    public boolean isFeatureEnabled(AuthFeatureKey authFeatureKey) {
        return lambdaQuery().eq(AuthSettingEntity::getFeatureKey, authFeatureKey.getCode())
                .eq(AuthSettingEntity::getEnabled, true)
                .count() > 0;
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
