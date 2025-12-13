package com.sky.AgentCore.dto.user;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 修改密码请求 */
@Data
public class ChangePasswordRequest {

    private String currentPassword;

    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]+$", message = "密码必须包含至少一个字母和一个数字")
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
