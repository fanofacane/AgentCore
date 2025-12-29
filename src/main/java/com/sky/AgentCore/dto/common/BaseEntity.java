package com.sky.AgentCore.dto.common;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.sky.AgentCore.dto.enums.Operator;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    @TableField(fill = FieldFill.INSERT)
    protected LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected LocalDateTime updatedAt;

    protected LocalDateTime deletedAt;

    @TableField(exist = false)
    private Operator operatedBy = Operator.USER;

    public boolean needCheckUserId() {
        return this.operatedBy == Operator.USER;
    }
}
