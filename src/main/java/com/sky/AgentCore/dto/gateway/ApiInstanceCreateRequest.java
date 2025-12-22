package com.sky.AgentCore.dto.gateway;

import lombok.Data;

@Data
public class ApiInstanceCreateRequest {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * API标识符，必填
     */
    private String apiIdentifier;

    /**
     * API类型，必填
     */
    private String apiType;

    /**
     * 业务ID，必填
     */
    private String businessId;

    public ApiInstanceCreateRequest() {
    }

    public ApiInstanceCreateRequest(String userId, String apiIdentifier, String apiType, String businessId) {
        this.userId = userId;
        this.apiIdentifier = apiIdentifier;
        this.apiType = apiType;
        this.businessId = businessId;
    }
}
