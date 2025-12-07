package com.sky.AgentCore.service;


public interface UserSettingsDomainService {
    /** 获取用户默认模型ID
     * @param userId 用户ID
     * @return 默认模型ID */
    String getUserDefaultModelId(String userId);
}
