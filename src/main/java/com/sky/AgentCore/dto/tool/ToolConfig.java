package com.sky.AgentCore.dto.tool;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ToolConfig implements Serializable {


    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工具唯一标识
     */
    private String toolId;

    /**
     * 工具名称（对应函数名）
     */
    private String name;

    /**
     * 工具描述（给AI的提示）
     */
    private String description;

    /**
     * 参数定义（JSON格式）
     * 注：若使用MyBatis-Plus，可搭配TypeHandler解析JSON为Map/自定义对象
     */
    private String parameters;

    /**
     * Spring容器中工具Bean名称
     */
    private String beanName;

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
