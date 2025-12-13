package com.sky.AgentCore.dto.captcha;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailCodeRequest {
    @Email(message = "不是一个合法的邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "验证码UUID不能为空")
    private String captchaUuid;

    @NotBlank(message = "图形验证码不能为空")
    private String captchaCode;
}
