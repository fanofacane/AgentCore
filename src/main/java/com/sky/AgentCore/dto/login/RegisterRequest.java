package com.sky.AgentCore.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "不是一个合法的邮箱")
    private String email;

    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    // 如果是邮箱注册，验证码必填
    private String code;
}
