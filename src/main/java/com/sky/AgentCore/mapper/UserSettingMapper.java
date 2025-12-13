package com.sky.AgentCore.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingMapper extends BaseMapper<UserSettingsEntity> {
}
