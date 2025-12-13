package com.sky.AgentCore.dto.auth;

import com.sky.AgentCore.dto.login.LoginMethodDTO;
import lombok.Data;

import java.util.Map;
@Data
public class AuthConfigDTO {
    private Map<String, LoginMethodDTO> loginMethods;
    private Boolean registerEnabled;
}
