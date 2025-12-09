package com.sky.AgentCore.service.serviceImpl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.user.UserSettingsConfig;
import com.sky.AgentCore.dto.user.UserSettingsDTO;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.dto.user.UserSettingsUpdateRequest;
import com.sky.AgentCore.mapper.UserSettingMapper;
import com.sky.AgentCore.service.UserSettingsDomainService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
        UserSettingsEntity userSettings = lambdaQuery().eq(UserSettingsEntity::getUserId, userId).one();
        UserSettingsConfig userSettingsConfig = JSON.parseObject(userSettings.getSettingConfig(), UserSettingsConfig.class);
        return new UserSettingsDTO(userSettings.getId(), userSettings.getUserId(), userSettingsConfig);
    }

    @Override
    public UserSettingsDTO updateUserSettings(UserSettingsUpdateRequest request, String userId) {
        UserSettingsEntity userSettings = new UserSettingsEntity();
        userSettings.setUserId(userId);
        userSettings.setSettingConfig(JSONUtil.toJsonStr(request.getSettingConfig()));
        LambdaUpdateChainWrapper<UserSettingsEntity> set = lambdaUpdate()
                .eq(UserSettingsEntity::getUserId, userId)
                .set(UserSettingsEntity::getSettingConfig, userSettings.getSettingConfig());
        userSettingMapper.update(null, set);
        return new UserSettingsDTO(userId,request.getSettingConfig());
    }
}
