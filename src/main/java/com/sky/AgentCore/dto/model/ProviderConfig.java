package com.sky.AgentCore.dto.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class ProviderConfig {
    private String apiKey;
    private String baseUrl;
    /** 模型 */
    private String model;
    private String protocol;
    private Map<String, String> customHeaders = new HashMap<>();

    public ProviderConfig(String apiKey, String baseUrl, String model, String protocol) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.protocol = protocol;
    }

    public ProviderConfig() {
    }
}
