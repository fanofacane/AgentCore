package com.sky.AgentCore.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.billing.UsageRecordEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsageRecordMapper extends BaseMapper<UsageRecordEntity> {
}
