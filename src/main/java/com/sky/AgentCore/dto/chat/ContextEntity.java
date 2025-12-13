package com.sky.AgentCore.dto.chat;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** 上下文实体类，管理会话的上下文窗口 */
@Data
@TableName("context")
public class ContextEntity {

    /** 上下文唯一ID */
    private String id;

    /** 所属会话ID */
    private String sessionId;

    /** 活跃消息ID列表 */
    private List<String> activeMessages = new ArrayList<>();

    /** 历史消息摘要 */
    private String summary;
}
