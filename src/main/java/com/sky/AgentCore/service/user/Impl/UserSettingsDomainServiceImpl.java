package com.sky.AgentCore.service.user.Impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.UserSettingsAssembler;
import com.sky.AgentCore.dto.FallbackConfig;
import com.sky.AgentCore.dto.user.UserSettingsConfig;
import com.sky.AgentCore.dto.user.UserSettingsDTO;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.dto.user.UserSettingsUpdateRequest;
import com.sky.AgentCore.mapper.UserSettingMapper;
import com.sky.AgentCore.service.user.UserSettingsDomainService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSettingsDomainServiceImpl extends ServiceImpl<UserSettingMapper, UserSettingsEntity> implements UserSettingsDomainService {
    @Resource
    private UserSettingMapper userSettingMapper;
    @Override
    public String getUserDefaultModelId(String userId) {
        UserSettingsDTO userSettings = getUserSettings(userId);
        return userSettings.getSettingConfig().getDefaultModel();
    }
    /** 获取用户设置
     * @param userId 用户ID
     * @return 用户设置实体 */
    public UserSettingsDTO getUserSettings(String userId) {
        UserSettingsEntity entity = lambdaQuery().eq(UserSettingsEntity::getUserId, userId).one();
        return UserSettingsAssembler.toDTO(entity);
    }

    @Override
    public UserSettingsDTO updateUserSettings(UserSettingsUpdateRequest request, String userId) {
        System.out.println("更新配置"+request);
        boolean success = false;
        boolean exists = lambdaQuery().eq(UserSettingsEntity::getUserId, userId).exists();
        if (!exists){
            UserSettingsEntity entity = UserSettingsAssembler.toEntity(request, userId);
            entity.setSettingConfig(request.getSettingConfig());
            success = save(entity);
        }else {
            success = lambdaUpdate().eq(UserSettingsEntity::getUserId, userId)
                    .set(UserSettingsEntity::getSettingConfig,
                            JSONUtil.toJsonStr(request.getSettingConfig())).update();
        }
        // 3. 更新结果校验，失败抛业务异常
        if (!success) throw new BusinessException("用户配置更新失败，配置不存在或无操作权限");

        return new UserSettingsDTO(userId,request.getSettingConfig());
    }
    /** 获取用户降级链配置
     * @param userId 用户ID
     * @return 降级模型ID列表，如果未启用降级则返回null */
    @Override
    public List<String> getUserFallbackChain(String userId) {
        UserSettingsEntity settings = lambdaQuery().eq(UserSettingsEntity::getUserId, userId).one();
        if (settings == null || settings.getSettingConfig() == null) return new ArrayList<>();

        FallbackConfig fallbackConfig = settings.getSettingConfig().getFallbackConfig();
        if (fallbackConfig == null || !fallbackConfig.isEnabled() || fallbackConfig.getFallbackChain().isEmpty()) {
            return new ArrayList<>();
        }

        return fallbackConfig.getFallbackChain();
    }

    @Override
    public void setUserDefaultModelId(String userId, String modelId) {
        UserSettingsEntity settings = lambdaQuery().eq(UserSettingsEntity::getUserId, userId).one();
        if (settings == null) {
            // 创建新的用户设置
            settings = new UserSettingsEntity();
            settings.setUserId(userId);
            settings.setDefaultModelId(modelId);
            save(settings);
        } else {
            // 更新现有设置
            settings.setDefaultModelId(modelId);
            lambdaUpdate().eq(UserSettingsEntity::getUserId, userId)
                            .set(UserSettingsEntity::getDefaultModelId, modelId).update();
        }
    }

}
