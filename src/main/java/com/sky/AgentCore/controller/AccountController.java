package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.account.AccountDTO;
import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.account.AccountAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 账户管理控制层 提供用户账户管理的API接口 */
@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountAppService accountAppService;
    /** 获取当前用户账户信息
     *
     * @return 账户信息 */
    @GetMapping("/current")
    public Result<AccountDTO> getCurrentUserAccount() {
        String userId = UserContext.getCurrentUserId();
        AccountDTO account = accountAppService.getUserAccount(userId);
        return Result.success(account);
    }
}