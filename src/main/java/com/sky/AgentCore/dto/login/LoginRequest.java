package com.sky.AgentCore.dto.login;

import lombok.Data;

@Data
public class LoginRequest {

    private String account;

    private String password;
}
