package com.sky.AgentCore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.agent.AgentEntity;
import com.sky.AgentCore.dto.agent.AgentVersionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AgentVersionMapper extends BaseMapper<AgentVersionEntity> {

    /** 根据名称和发布状态查询所有助理的最新版本 同时支持只按状态查询（当name为空时） */
    @Select({"<script>", "SELECT v.* FROM agent_versions v ", "INNER JOIN (",
            "    SELECT agent_id, MAX(published_at) as latest_date ", "    FROM agent_versions ",
            "    WHERE deleted_at IS NULL ", "    <if test='status != null'>",
            "        AND publish_status = #{status} ", "    </if>", "    GROUP BY agent_id",
            ") latest ON v.agent_id = latest.agent_id AND v.published_at = latest.latest_date ",
            "WHERE v.deleted_at IS NULL ", "<if test='name != null and name != \"\"'>",
            "    AND v.name LIKE CONCAT('%', #{name}, '%') ", "</if>", "<if test='status != null'>",
            "    AND v.publish_status = #{status} ", "</if>", "</script>"})
    List<AgentVersionEntity> selectLatestVersionsByNameAndStatus(String name, Integer status);
}
