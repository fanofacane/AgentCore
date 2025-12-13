package com.sky.AgentCore.dto.login;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailLoginRequest {
    @Email(message = "不是一个合法的邮箱")
    private String email;
    // 如果是邮箱注册，验证码必填
    private String code;
}
