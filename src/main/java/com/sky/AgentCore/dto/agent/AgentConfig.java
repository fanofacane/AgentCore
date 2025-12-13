package com.sky.AgentCore.dto.agent;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AgentConfig implements Serializable {


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 智能体唯一标识
     */
    private String agentId;

    /**
     * 智能体名称
     */
    private String name;

    /**
     * 角色描述（system prompt）
     */
    private String roleDesc;

    /**
     * 关联工具ID列表（逗号分隔）
     */
    private String toolIds;

    /**
     * 默认温度参数
     */
    private BigDecimal temperature = new BigDecimal("0.7");

    /**
     * 默认最大 tokens
     */
    private Integer maxTokens = 2048;

    /**
     * 是否启用
     */
    private Boolean isEnabled = true;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
