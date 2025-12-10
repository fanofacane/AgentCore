package com.sky.AgentCore.enums;

import com.sky.AgentCore.Exceptions.BusinessException;

public enum Role {

    USER, SYSTEM, ASSISTANT,SUMMARY;

    public static Role fromCode(String code) {
        for (Role role : values()) {
            if (role.name().equals(code)) {
                return role;
            }
        }
        throw new BusinessException("Unknown model type code: " + code);
    }
}
