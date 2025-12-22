package com.sky.AgentCore.dto.gateway;

import lombok.Data;

import java.util.List;
@Data
public class SelectInstanceRequest {

    /**
     * 用户ID，可选
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
     * 亲和性键，用于会话绑定，可选
     */
    private String affinityKey;

    /**
     * 亲和性类型，可选 定义亲和性的类型，例如：SESSION、USER、BATCH、REGION等
     */
    private String affinityType;

    /**
     * 降级链，按优先级排序的模型ID列表
     */
    private List<String> fallbackChain;

    public SelectInstanceRequest() {
    }

    public SelectInstanceRequest(String userId, String apiIdentifier, String apiType) {
        this.userId = userId;
        this.apiIdentifier = apiIdentifier;
        this.apiType = apiType;
    }

    public SelectInstanceRequest(String userId, String apiIdentifier, String apiType, String affinityKey,
                                 String affinityType) {
        this.userId = userId;
        this.apiIdentifier = apiIdentifier;
        this.apiType = apiType;
        this.affinityKey = affinityKey;
        this.affinityType = affinityType;
    }
}
