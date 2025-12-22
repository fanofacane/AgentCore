package com.sky.AgentCore.dto.agent;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.LLMModelConfigConverter;
import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

/** Agent工作区实体类 用于记录用户添加到工作区的Agent */
@Data
@TableName(value = "agent_workspace", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class AgentWorkspaceEntity extends BaseEntity {

    /** 主键ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** Agent ID */
    @TableField("agent_id")
    private String agentId;

    /** 用户ID */
    @TableField("user_id")
    private String userId;

    /** 模型配置 */
    @TableField(value = "llm_model_config", typeHandler = LLMModelConfigConverter.class, jdbcType = JdbcType.OTHER)
    private LLMModelConfig llmModelConfig;

    public AgentWorkspaceEntity(String agentId, String userId, LLMModelConfig llmModelConfig) {
        this.agentId = agentId;
        this.userId = userId;
        this.llmModelConfig = llmModelConfig;
    }

    public AgentWorkspaceEntity(String agentId) {
        this.agentId = agentId;
    }
    public AgentWorkspaceEntity(LLMModelConfig llmModelConfig) {
        this.llmModelConfig = llmModelConfig;
    }
}
