package com.sky.AgentCore.service.login;

public interface SsoAppService {
    String getSsoLoginUrl(String provider, String redirectUrl);

    String handleSsoCallback(String provider, String code);
}
