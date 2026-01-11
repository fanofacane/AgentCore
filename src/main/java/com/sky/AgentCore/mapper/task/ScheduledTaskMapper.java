package com.sky.AgentCore.mapper.task;

import com.sky.AgentCore.dto.scheduledtask.ScheduledTaskEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScheduledTaskMapper extends MyBatisPlusExtMapper<ScheduledTaskEntity> {
}
