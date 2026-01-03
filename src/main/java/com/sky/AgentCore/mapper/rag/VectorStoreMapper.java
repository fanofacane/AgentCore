package com.sky.AgentCore.mapper.rag;

import com.sky.AgentCore.dto.rag.model.VectorStoreResult;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface VectorStoreMapper extends MyBatisPlusExtMapper<VectorStoreResult> {

    @Select({
            "<script>",
            "SELECT ",
            "    embedding_id,",
            "    text,",
            "    metadata,",
            "    ts_rank_cd(",
            "        to_tsvector('simple', text),", // 移除 unaccent
            "        to_tsquery(",
            "            'simple',",
            "            replace(plainto_tsquery('simple', #{userQuery})::text, '&amp;', '|')", // 移除 unaccent
            "        )",
            "    ) AS score",
            "FROM",
            "    vector_store",
            "WHERE",
            "    (metadata ->> 'DATA_SET_ID') IN",
            "    <foreach collection='dataSetIds' item='dataSetId' open='(' separator=',' close=')'>",
            "        #{dataSetId}",
            "    </foreach>",
            "    AND",
            "    to_tsvector('simple', text) @@ to_tsquery(",
            "        'simple',",
            "        replace(plainto_tsquery('simple', #{userQuery})::text, '&amp;', '|')", // 移除 unaccent
            "    )",
            "ORDER BY",
            "    score DESC",
            "LIMIT #{maxResults}",
            "</script>"
    })
    List<VectorStoreResult> keywordSearch(
            @Param("dataSetIds") List<String> dataSetIds,
            @Param("userQuery") String userQuery,
            @Param("maxResults") Integer maxResults
    );
}
