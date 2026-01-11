package com.sky.AgentCore.mapper.agent;

import com.sky.AgentCore.dto.trace.SessionTraceStatisticsDTO;
import com.sky.AgentCore.dto.trace.AgentExecutionSummaryEntity;
import com.sky.AgentCore.mapper.MyBatisPlusExtMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/** Agent执行链路汇总仓库接口 */
@Mapper
public interface AgentExecutionSummaryMapper extends MyBatisPlusExtMapper<AgentExecutionSummaryEntity> {

    @Select("""
            SELECT COUNT(*)
            FROM (
                SELECT aes.session_id
                FROM agent_execution_summary aes
                JOIN sessions s ON s.id = aes.session_id AND s.user_id = aes.user_id
                WHERE aes.user_id = #{userId}
                  AND aes.agent_id = #{agentId}
                  AND (CAST(#{startTime} AS timestamp) IS NULL OR aes.execution_start_time >= CAST(#{startTime} AS timestamp))
                  AND (CAST(#{endTime} AS timestamp) IS NULL OR aes.execution_start_time <= CAST(#{endTime} AS timestamp))
                  AND (COALESCE(CAST(#{includeArchived} AS boolean), FALSE) = TRUE OR s.is_archived = FALSE)
                  AND (CAST(#{keyword} AS text) IS NULL OR s.title ILIKE CONCAT('%', CAST(#{keyword} AS text), '%'))
                GROUP BY aes.session_id
                HAVING (
                    CAST(#{hasSuccessfulExecution} AS boolean) IS NULL
                    OR (
                        CAST(#{hasSuccessfulExecution} AS boolean) = TRUE
                        AND SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END) > 0
                    )
                    OR (
                        CAST(#{hasSuccessfulExecution} AS boolean) = FALSE
                        AND SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END) = 0
                    )
                )
            ) t
            """)
    long countAgentSessionStatistics(@Param("agentId") String agentId,
                                    @Param("userId") String userId,
                                    @Param("keyword") String keyword,
                                    @Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime,
                                    @Param("hasSuccessfulExecution") Boolean hasSuccessfulExecution,
                                    @Param("includeArchived") Boolean includeArchived);

    @Select("""
            WITH filtered AS (
                SELECT
                    aes.session_id AS session_id,
                    aes.agent_id AS agent_id,
                    COUNT(*) AS total_executions,
                    SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END) AS successful_executions,
                    (COUNT(*) - SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END)) AS failed_executions,
                    COALESCE(SUM(aes.total_tokens), 0) AS total_tokens,
                    COALESCE(SUM(aes.total_input_tokens), 0) AS total_input_tokens,
                    COALESCE(SUM(aes.total_output_tokens), 0) AS total_output_tokens,
                    COALESCE(SUM(aes.tool_call_count), 0) AS total_tool_calls,
                    COALESCE(SUM(aes.total_execution_time), 0) AS total_execution_time,
                    MAX(aes.execution_start_time) AS last_execution_time
                FROM agent_execution_summary aes
                JOIN sessions s ON s.id = aes.session_id AND s.user_id = aes.user_id
                WHERE aes.user_id = #{userId}
                  AND aes.agent_id = #{agentId}
                  AND (CAST(#{startTime} AS timestamp) IS NULL OR aes.execution_start_time >= CAST(#{startTime} AS timestamp))
                  AND (CAST(#{endTime} AS timestamp) IS NULL OR aes.execution_start_time <= CAST(#{endTime} AS timestamp))
                  AND (COALESCE(CAST(#{includeArchived} AS boolean), FALSE) = TRUE OR s.is_archived = FALSE)
                  AND (CAST(#{keyword} AS text) IS NULL OR s.title ILIKE CONCAT('%', CAST(#{keyword} AS text), '%'))
                GROUP BY aes.session_id, aes.agent_id
                HAVING (
                    CAST(#{hasSuccessfulExecution} AS boolean) IS NULL
                    OR (
                        CAST(#{hasSuccessfulExecution} AS boolean) = TRUE
                        AND SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END) > 0
                    )
                    OR (
                        CAST(#{hasSuccessfulExecution} AS boolean) = FALSE
                        AND SUM(CASE WHEN aes.execution_success IS TRUE THEN 1 ELSE 0 END) = 0
                    )
                )
            )
            SELECT
                f.session_id AS "sessionId",
                s.title AS "sessionTitle",
                f.agent_id AS "agentId",
                f.total_executions AS "totalExecutions",
                f.successful_executions AS "successfulExecutions",
                f.failed_executions AS "failedExecutions",
                CASE
                    WHEN f.total_executions > 0 THEN (f.successful_executions::double precision / f.total_executions)
                    ELSE 0
                END AS "successRate",
                f.total_tokens AS "totalTokens",
                f.total_input_tokens AS "totalInputTokens",
                f.total_output_tokens AS "totalOutputTokens",
                f.total_tool_calls AS "totalToolCalls",
                f.total_execution_time AS "totalExecutionTime",
                s.created_at AS "sessionCreatedTime",
                f.last_execution_time AS "lastExecutionTime",
                (
                    SELECT aes2.execution_success
                    FROM agent_execution_summary aes2
                    WHERE aes2.user_id = #{userId}
                      AND aes2.agent_id = #{agentId}
                      AND aes2.session_id = f.session_id
                      AND (CAST(#{startTime} AS timestamp) IS NULL OR aes2.execution_start_time >= CAST(#{startTime} AS timestamp))
                      AND (CAST(#{endTime} AS timestamp) IS NULL OR aes2.execution_start_time <= CAST(#{endTime} AS timestamp))
                    ORDER BY aes2.execution_start_time DESC, aes2.id DESC
                    LIMIT 1
                ) AS "lastExecutionSuccess",
                s.is_archived AS "isArchived"
            FROM filtered f
            JOIN sessions s ON s.id = f.session_id
            ORDER BY f.last_execution_time DESC
            LIMIT CAST(#{limit} AS bigint) OFFSET CAST(#{offset} AS bigint)
            """)
    List<SessionTraceStatisticsDTO> selectAgentSessionStatisticsPage(@Param("agentId") String agentId,
                                                                    @Param("userId") String userId,
                                                                    @Param("keyword") String keyword,
                                                                    @Param("startTime") LocalDateTime startTime,
                                                                    @Param("endTime") LocalDateTime endTime,
                                                                    @Param("hasSuccessfulExecution") Boolean hasSuccessfulExecution,
                                                                    @Param("includeArchived") Boolean includeArchived,
                                                                    @Param("offset") long offset,
                                                                    @Param("limit") long limit);
}
