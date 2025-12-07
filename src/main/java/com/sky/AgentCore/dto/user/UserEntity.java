package com.sky.AgentCore.dto.user;


import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

@Data
@TableName("users")
public class UserEntity extends BaseEntity {
    private String id;
    private String nickname;
    private String email;
    private String phone;
    private String password;
    private String githubId;
    private String githubLogin;
    private String avatarUrl;
    private String loginPlatform;
    private Boolean isAdmin;
    public void valid() {
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(githubId)) {
            throw new BusinessException("必须使用邮箱、手机号或GitHub账号来作为账号");
        }
    }
}
