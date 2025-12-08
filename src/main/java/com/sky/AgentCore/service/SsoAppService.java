package com.sky.AgentCore.service;

public interface SsoAppService {
    String getSsoLoginUrl(String provider, String redirectUrl);

    String handleSsoCallback(String provider, String code);
}
