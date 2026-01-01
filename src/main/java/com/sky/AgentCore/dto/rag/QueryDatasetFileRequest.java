package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.tool.Page;

public class QueryDatasetFileRequest extends Page {

    /** 搜索关键词 */
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
