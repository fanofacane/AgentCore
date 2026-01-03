package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.tool.Page;
import jakarta.validation.constraints.NotBlank;

/** 查询文件语料请求 */
public class QueryDocumentUnitsRequest extends Page {

    /** 文件ID */
    @NotBlank(message = "文件ID不能为空")
    private String fileId;

    /** 内容关键词搜索 */
    private String keyword;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
