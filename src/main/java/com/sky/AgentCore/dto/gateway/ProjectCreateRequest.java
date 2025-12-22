package com.sky.AgentCore.dto.gateway;


import lombok.Data;

/** 项目创建请求
 *
 * @author fanofacane
 * @since 1.0.0 */
@Data
public class ProjectCreateRequest {

    /** 项目名称 */
    private String name;

    /** 项目描述 */
    private String description;

    /** API密钥 */
    private String apiKey;

    public ProjectCreateRequest() {
    }

    public ProjectCreateRequest(String name, String description, String apiKey) {
        this.name = name;
        this.description = description;
        this.apiKey = apiKey;
    }
}