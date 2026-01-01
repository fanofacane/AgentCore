package com.sky.AgentCore.mapper;

import com.sky.AgentCore.dto.trace.AgentExecutionSummaryEntity;
import org.apache.ibatis.annotations.Mapper;

/** Agent执行链路汇总仓库接口 */
@Mapper
public interface AgentExecutionSummaryMapper extends MyBatisPlusExtMapper<AgentExecutionSummaryEntity> {
}
