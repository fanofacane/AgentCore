package com.sky.AgentCore.controller.login;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.login.SsoAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sso")
public class SsoController {
    @Autowired
    private SsoAppService ssoAppService;

    /** 获取SSO登录URL
     * @param provider SSO提供商（community、github等）
     * @param redirectUrl 登录成功后的回调地址
     * @return 登录URL */
    @GetMapping("/{provider}/login")
    public Result<Map<String, String>> getSsoLoginUrl(@PathVariable String provider,
                                                      @RequestParam(required = false) String redirectUrl) {
        System.out.println("开始获取SSO登录URL");
        String loginUrl = ssoAppService.getSsoLoginUrl(provider, redirectUrl);
        return Result.success(Map.of("loginUrl", loginUrl));
    }

    /** SSO登录回调处理
     * @param provider SSO提供商
     * @param code 授权码
     * @return 登录token */
    @GetMapping("/{provider}/callback")
    public Result<Map<String, Object>> handleSsoCallback(@PathVariable String provider, @RequestParam String code) {
        System.out.println("开始处理SSO回调");
        String token = ssoAppService.handleSsoCallback(provider, code);
        return Result.success("登录成功", Map.of("token", token));
    }
}