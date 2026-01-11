package com.sky.AgentCore.mapper.session;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SessionMapper extends MyBatisPlusExtMapper<SessionEntity> {
}
