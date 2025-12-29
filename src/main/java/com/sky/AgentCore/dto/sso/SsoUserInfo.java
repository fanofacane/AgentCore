package com.sky.AgentCore.dto.sso;

import com.sky.AgentCore.dto.enums.SsoProvider;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SsoUserInfo {
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String desc;
    private SsoProvider provider;
}