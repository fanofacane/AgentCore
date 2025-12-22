package com.sky.AgentCore.dto.gateway;

import lombok.Data;

import java.util.Map;
@Data
public class ApiInstanceDTO {

    /** 实例ID */
    private String id;

    /** 项目ID */
    private String projectId;

    /** 项目名称 */
    private String projectName;

    /** 用户ID */
    private String userId;

    /** API标识符 */
    private String apiIdentifier;

    /** API类型 */
    private String apiType;

    /** 业务ID */
    private String businessId;

    /** 路由参数 */
    private Map<String, Object> routingParams;

    /** 实例状态 */
    private String status;

    /** 元数据 */
    private Map<String, Object> metadata;
    @Override
    public String toString() {
        return "ApiInstanceDTO{" + "id='" + id + '\'' + ", projectId='" + projectId + '\'' + ", projectName='"
                + projectName + '\'' + ", userId='" + userId + '\'' + ", apiIdentifier='" + apiIdentifier + '\''
                + ", apiType='" + apiType + '\'' + ", businessId='" + businessId + '\'' + ", routingParams="
                + routingParams + ", status='" + status + '\'' + ", metadata=" + metadata + '}';
    }
}
