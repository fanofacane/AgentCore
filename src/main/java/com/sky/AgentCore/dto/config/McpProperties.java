package com.sky.AgentCore.dto.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MCP配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "agent.mcp")
public class McpProperties {
    /**
     * MCP服务器列表
     */
    private List<McpServerConfig> servers;

    @Data
    public static class McpServerConfig {
        /**
         * 服务名称
         */
        private String name;
        
        /**
         * 服务地址 (SSE URL)
         */
        private String url;
        
        /**
         * 超时时间(分钟)，默认5分钟
         */
        private Integer timeoutMinutes = 5;
    }
}
