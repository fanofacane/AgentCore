package com.sky.AgentCore.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.mapper.UserSettingMapper;
import com.sky.AgentCore.service.UserSettingsDomainService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsDomainServiceImpl implements UserSettingsDomainService {
    @Resource
    private UserSettingMapper userSettingMapper;
    @Override
    public String getUserDefaultModelId(String userId) {
        UserSettingsEntity settings = getUserSettings(userId);
        return settings != null ? settings.getSettingConfig() : null;
    }
    /** 获取用户设置
     * @param userId 用户ID
     * @return 用户设置实体 */
    public UserSettingsEntity getUserSettings(String userId) {
        LambdaQueryWrapper<UserSettingsEntity> wrapper = Wrappers.<UserSettingsEntity>lambdaQuery()
                .eq(UserSettingsEntity::getUserId, userId);
       return userSettingMapper.selectOne(wrapper);
    }
}
