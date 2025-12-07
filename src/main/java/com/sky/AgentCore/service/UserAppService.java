package com.sky.AgentCore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.user.ChangePasswordRequest;
import com.sky.AgentCore.dto.user.UserDTO;
import com.sky.AgentCore.dto.user.UserEntity;
import com.sky.AgentCore.dto.user.UserUpdateRequest;

public interface UserAppService extends IService<UserEntity> {
    UserDTO getUserInfo(String userId);

    void updateUserInfo(UserUpdateRequest userUpdateRequest, String userId);

    void changePassword(ChangePasswordRequest request, String userId);
}
