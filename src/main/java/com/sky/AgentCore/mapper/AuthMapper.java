package com.sky.AgentCore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.auth.AuthSetting;
import com.sky.AgentCore.dto.auth.AuthSettingEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<AuthSetting> {
}
