package com.sky.AgentCore.dto.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.converter.InstallTypeConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.InstallType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@TableName("user_rags")
public class UserRagEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    /** 安装记录ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 用户ID */
    private String userId;

    /** 关联的RAG版本快照ID */
    private String ragVersionId;

    /** 安装时的名称 */
    private String name;

    /** 安装时的描述 */
    private String description;

    /** 安装时的图标 */
    private String icon;

    /** 版本号 */
    private String version;

    /** 安装时间 */
    private LocalDateTime installedAt;

    /** 原始RAG数据集ID */
    private String originalRagId;

    /** 安装类型 */
    @TableField(value = "install_type", typeHandler = InstallTypeConverter.class)
    private InstallType installType = InstallType.SNAPSHOT;


    /** 检查是否为引用类型安装
     *
     * @return 是否为引用类型 */
    public boolean isReferenceType() {
        return this.installType != null && this.installType.isReference();
    }

    /** 检查是否为快照类型安装
     *
     * @return 是否为快照类型 */
    public boolean isSnapshotType() {
        return this.installType != null && this.installType.isSnapshot();
    }
}
