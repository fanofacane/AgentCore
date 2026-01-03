package com.sky.AgentCore.mapper.llm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.model.ProviderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProvidersMapper extends BaseMapper<ProviderEntity> {
}
