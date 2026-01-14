package com.sky.AgentCore.mapper.rag;

import com.sky.AgentCore.dto.rag.RagQaDatasetEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RagQaDatasetMapper extends MyBatisPlusExtMapper<RagQaDatasetEntity> {

    @Select("select * from ai_rag_qa_dataset where id = #{originalRagId}")
    RagQaDatasetEntity getDatasetByIdForDelete(String originalRagId);

    @Select("SELECT * FROM rag_qa_dataset WHERE user_id = #{userId}" +
            "AND deleted_at IS NULL ORDER BY created_at DESC" )
    List<RagQaDatasetEntity> selectAll(String userId);
}
