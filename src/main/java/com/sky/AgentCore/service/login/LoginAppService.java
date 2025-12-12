package com.sky.AgentCore.service.login;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.login.EmailLoginRequest;
import com.sky.AgentCore.dto.login.LoginRequest;
import com.sky.AgentCore.dto.login.RegisterRequest;
import com.sky.AgentCore.dto.user.UserEntity;

public interface LoginAppService extends IService<UserEntity> {
    String login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    void sendEmailVerificationCode(String email, String captchaUuid, String captchaCode, String clientIp, String businessType);

    void sendResetPasswordCode(String email, String captchaUuid, String captchaCode, String clientIp);

    String emailLogin(EmailLoginRequest emailLoginRequest);

    void sendEmailLoginVerificationCode(String email, String captchaUuid, String captchaCode, String clientIp, String businessTypeEmailLogin);
}
