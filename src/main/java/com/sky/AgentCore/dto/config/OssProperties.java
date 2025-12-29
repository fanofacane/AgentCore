package com.sky.AgentCore.dto.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** OSS对象存储配置 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /** 访问端点 */
    private String endpoint;

    /** 自定义域名 */
    private String customDomain;

    /** 访问密钥 */
    private String accessKey;

    /** 秘密密钥 */
    private String secretKey;

    /** 存储桶名称 */
    private String bucketName;

    /** 区域 */
    private String region = "cn-beijing";

    /** 启用路径样式访问 */
    private boolean pathStyleAccess = false;

    /** 文件访问URL前缀 */
    private String urlPrefix;
}
