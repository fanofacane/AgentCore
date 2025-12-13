package com.sky.AgentCore.dto.common;


import com.baomidou.mybatisplus.annotation.TableField;
import com.sky.AgentCore.enums.Operator;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

    protected LocalDateTime deletedAt;

    @TableField(exist = false)
    private Operator operatedBy = Operator.USER;

    public boolean needCheckUserId() {
        return this.operatedBy == Operator.USER;
    }
}
