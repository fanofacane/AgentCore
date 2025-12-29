package com.sky.AgentCore.dto.model;

import com.sky.AgentCore.dto.enums.ProviderProtocol;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class ProviderConfig {
    private String apiKey;
    private String baseUrl;
    /** 模型 */
    private String model;
    private ProviderProtocol protocol;
    private Map<String, String> customHeaders = new HashMap<>();

    public ProviderConfig(String apiKey, String baseUrl, String model, ProviderProtocol protocol) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.protocol = protocol;
    }

    public ProviderConfig() {
    }
}
