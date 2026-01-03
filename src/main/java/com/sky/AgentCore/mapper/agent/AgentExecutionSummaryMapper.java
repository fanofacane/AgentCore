package com.sky.AgentCore.mapper.agent;

import com.sky.AgentCore.dto.trace.AgentExecutionSummaryEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;

/** Agent执行链路汇总仓库接口 */
@Mapper
public interface AgentExecutionSummaryMapper extends MyBatisPlusExtMapper<AgentExecutionSummaryEntity> {
}
