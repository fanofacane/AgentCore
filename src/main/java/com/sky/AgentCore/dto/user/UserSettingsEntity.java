package com.sky.AgentCore.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

/** 用户设置领域模型 */
@Data
@TableName("user_settings")
public class UserSettingsEntity extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设置配置
     */
    private String settingConfig;
}
