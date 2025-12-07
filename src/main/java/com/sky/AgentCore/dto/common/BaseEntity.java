package com.sky.AgentCore.dto.common;


import lombok.Data;

import java.time.LocalDateTime;
@Data
public class BaseEntity {
    protected LocalDateTime createdAt;

    protected LocalDateTime updatedAt;

    protected LocalDateTime deletedAt;

//    private Operator operatedBy = Operator.USER;
}
