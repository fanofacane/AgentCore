package com.sky.AgentCore.dto.rag;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** RAG知识库数据集实体
 * @author fanofacane
 */
@Data
@TableName("ai_rag_qa_dataset")
public class RagQaDatasetEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5803685552931418952L;

    /** 数据集ID */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 数据集名称 */
    private String name;

    /** 数据集图标 */
    private String icon;

    /** 数据集说明 */
    private String description;

    /** 用户ID */
    private String userId;
}
