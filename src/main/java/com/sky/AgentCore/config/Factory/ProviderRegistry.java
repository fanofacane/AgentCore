package com.sky.AgentCore.config.Factory;

import com.sky.AgentCore.enums.ProviderProtocol;
import com.sky.AgentCore.service.llm.provider.Provider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProviderRegistry {
    private final Map<ProviderProtocol, Provider> providers = new HashMap<>();

    public ProviderRegistry(List<Provider> providerList) {
        for (Provider provider : providerList) {
            providers.put(provider.getProtocol(), provider);
        }
    }

    public Provider get(ProviderProtocol protocol) {
        return providers.get(protocol);
    }
}

