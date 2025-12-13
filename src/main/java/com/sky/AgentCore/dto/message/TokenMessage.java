package com.sky.AgentCore.dto.message;


import lombok.Data;

import java.time.LocalDateTime;

/** Token领域的消息模型 只包含Token计算所需的必要信息 */
@Data
public class TokenMessage {

    /** 消息ID */
    private String id;

    /** 消息内容 */
    private String content;

    /** 消息角色 */
    private String role;

    /** 消息Token数量 */
    private Integer tokenCount;

    /** 消息本体Token数量 */
    private Integer bodyTokenCount;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 默认构造函数 */
    public TokenMessage() {
        this.createdAt = LocalDateTime.now();
    }

    /** 带参数的构造函数 */
    public TokenMessage(String id, String content, String role, Integer tokenCount) {
        this.id = id;
        this.content = content;
        this.role = role;
        this.tokenCount = tokenCount;
        this.createdAt = LocalDateTime.now();
    }

    /** 完整参数的构造函数 */
    public TokenMessage(String id, String content, String role, Integer tokenCount, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.role = role;
        this.tokenCount = tokenCount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /** 添加带有content和role参数的构造函数 */
    public TokenMessage(String content, String role) {
        this.content = content;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
