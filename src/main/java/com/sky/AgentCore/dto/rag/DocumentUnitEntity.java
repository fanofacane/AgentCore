package com.sky.AgentCore.dto.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.dto.common.BaseEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
@TableName("document_unit")
public class DocumentUnitEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7001509997040094844L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 文档ID */
    private String fileId;

    /** 页码 */
    private Integer page;

    /** 当前页内容 */
    private String content;

    /** 是否进行向量化 */
    private Boolean isVector;

    /** ocr识别状态 */
    private Boolean isOcr;

    /** 相似度分数（非持久化字段，用于RAG搜索结果） */
    @TableField(exist = false)
    private Double similarityScore;

}
