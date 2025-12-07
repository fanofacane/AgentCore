package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.auth.AuthConfigDTO;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.AuthSettingAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 认证配置控制器（用户端） */
@RestController
@RequestMapping("/auth")
public class AuthConfigController {
    @Autowired
    private AuthSettingAppService authSettingAppService;
    /** 获取可用的认证配置
     *
     * @return 认证配置 */
    @GetMapping("/config")
    public Result<AuthConfigDTO> getAuthConfig() {
        AuthConfigDTO config = authSettingAppService.getAuthConfig();
        System.out.println(config);
        return Result.success(config);
    }
}
