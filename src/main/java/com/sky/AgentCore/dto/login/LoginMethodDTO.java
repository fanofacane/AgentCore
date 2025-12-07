package com.sky.AgentCore.dto.login;

import lombok.Data;

/** 登录方式DTO */
@Data
public class LoginMethodDTO {

    private Boolean enabled;
    private String name;
    private String provider;
}
