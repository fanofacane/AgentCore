package com.sky.AgentCore.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.auth.AuthSetting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthMapper extends BaseMapper<AuthSetting> {
}
