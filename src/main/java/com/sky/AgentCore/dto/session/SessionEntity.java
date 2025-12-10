package com.sky.AgentCore.dto.session;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/** 会话实体类，代表一个独立的对话会话/主题 */
@Data
@TableName("sessions")
public class SessionEntity extends BaseEntity {

    /** 会话唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 会话标题 */
    @TableField("title")
    private String title;

    /** 所属用户ID */
    @TableField("user_id")
    private String userId;

    /** 关联的Agent版本ID */
    @TableField("agent_id")
    private String agentId;

    /** 会话描述 */
    @TableField("description")
    private String description;

    /** 是否归档 */
    @TableField("is_archived")
    private boolean isArchived;

    /** 会话元数据，可存储其他自定义信息 */
    @TableField("metadata")
    private String metadata;

    /** 创建新会话 */
    public static SessionEntity createNew(String title, String userId) {
        SessionEntity session = new SessionEntity();
        session.setTitle(title);
        session.setUserId(userId);
        session.setArchived(false);
        return session;
    }

    /** 更新会话信息 */
    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        // 自动填充会处理更新时间
    }

    /** 归档会话 */
    public void archive() {
        this.isArchived = true;
        // 自动填充会处理更新时间
    }

    /** 恢复已归档会话 */
    public void unarchive() {
        this.isArchived = false;
        this.updatedAt = LocalDateTime.now();
    }
}
