package com.sky.AgentCore.service.user.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.login.ChangePasswordRequest;
import com.sky.AgentCore.dto.user.UserDTO;
import com.sky.AgentCore.dto.user.UserEntity;
import com.sky.AgentCore.dto.user.UserUpdateRequest;
import com.sky.AgentCore.mapper.user.UserMapper;
import com.sky.AgentCore.service.user.UserService;
import com.sky.AgentCore.utils.PasswordUtils;
import org.springframework.stereotype.Service;

@Service
public class UserAppServiceImpl extends ServiceImpl<UserMapper,UserEntity> implements UserService {
    @Override
    public UserDTO getUserInfo(String userId) {
        UserDTO userDTO = new UserDTO();
        UserEntity userEntity = getById(userId);
        BeanUtil.copyProperties(userEntity,userDTO);
        return userDTO;
    }

    @Override
    public void updateUserInfo(UserUpdateRequest userUpdateRequest, String userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setNickname(userUpdateRequest.getNickname());
        boolean b = updateById(userEntity);
        if (!b) throw new BusinessException("更新用户信息失败");
    }

    @Override
    public void changePassword(ChangePasswordRequest request, String userId) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) throw new BusinessException("新密码和确认密码不一致");
        UserEntity user = getById(userId);

        if(!PasswordUtils.matches(request.getCurrentPassword(),user.getPassword())) throw new BusinessException("当前密码错误");
        if ((PasswordUtils.matches(request.getNewPassword(),user.getPassword()))) throw new BusinessException("新密码不能与当前密码相同");

        user.setPassword(PasswordUtils.encode(request.getNewPassword()));
    }
}
