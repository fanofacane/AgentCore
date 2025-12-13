package com.sky.AgentCore.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserDTO {

    private String id;

    private String nickname;

    private String email;

    private String phone;

    private String githubId;

    private String githubLogin;

    private String avatarUrl;

    private String loginPlatform;

    private Boolean isAdmin;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
