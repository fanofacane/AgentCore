package com.sky.AgentCore.dto.gateway;

import lombok.Data;

import java.util.Map;

/** API实例更新请求
 *
 * @author fanofacane
 * @since 1.0.0 */
@Data
public class ApiInstanceUpdateRequest {

    private String userId;

    private String apiIdentifier;

    private Map<String, Object> routingParams;

    private Map<String, Object> metadata;

    public ApiInstanceUpdateRequest() {
    }

    public ApiInstanceUpdateRequest(String userId, String apiIdentifier, Map<String, Object> routingParams,
                                    Map<String, Object> metadata) {
        this.userId = userId;
        this.apiIdentifier = apiIdentifier;
        this.routingParams = routingParams;
        this.metadata = metadata;
    }
    @Override
    public String toString() {
        return "ApiInstanceUpdateRequest{" + "userId='" + userId + '\'' + ", apiIdentifier='" + apiIdentifier + '\''
                + ", routingParams=" + routingParams + ", metadata=" + metadata + '}';
    }
}
