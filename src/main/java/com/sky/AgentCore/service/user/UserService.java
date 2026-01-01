package com.sky.AgentCore.service.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.login.ChangePasswordRequest;
import com.sky.AgentCore.dto.user.UserDTO;
import com.sky.AgentCore.dto.user.UserEntity;
import com.sky.AgentCore.dto.user.UserUpdateRequest;

public interface UserService extends IService<UserEntity> {
    UserDTO getUserInfo(String userId);

    void updateUserInfo(UserUpdateRequest userUpdateRequest, String userId);

    void changePassword(ChangePasswordRequest request, String userId);
}
