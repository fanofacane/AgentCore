package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.user.ChangePasswordRequest;
import com.sky.AgentCore.dto.user.UserDTO;
import com.sky.AgentCore.dto.user.UserUpdateRequest;
import com.sky.AgentCore.service.UserAppService;
import com.sky.AgentCore.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/** 用户 */
@RestController
@RequestMapping("/users")
public class PortalUserController {
    @Autowired
    private UserAppService userAppService;
    /** 获取用户信息
     * @return */
    @GetMapping
    public Result<UserDTO> getUserInfo() {
        String userId = UserContext.getCurrentUserId();
        return Result.success(userAppService.getUserInfo(userId));
    }
    /** 修改用户信息
     * @param userUpdateRequest 需要修改的信息
     * @return */
    @PostMapping
    public Result<?> updateUserInfo(@RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        String userId = UserContext.getCurrentUserId();
        userAppService.updateUserInfo(userUpdateRequest, userId);
        return Result.success();
    }
    /** 修改密码
     *
     * @param request 修改密码请求
     * @return 修改结果 */
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody @Validated ChangePasswordRequest request) {
        String userId = UserContext.getCurrentUserId();
        userAppService.changePassword(request, userId);
        return Result.success().message("密码修改成功");
    }

}
