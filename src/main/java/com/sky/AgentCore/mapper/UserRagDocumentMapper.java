package com.sky.AgentCore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.rag.UserRagDocumentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRagDocumentMapper extends MyBatisPlusExtMapper<UserRagDocumentEntity> {
}
