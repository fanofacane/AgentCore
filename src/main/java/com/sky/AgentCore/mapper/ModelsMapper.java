package com.sky.AgentCore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.model.ModelEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModelsMapper extends BaseMapper<ModelEntity> {
}
