package com.sky.AgentCore.mapper.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.user.UserSettingsEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSettingMapper extends MyBatisPlusExtMapper<UserSettingsEntity> {
}
