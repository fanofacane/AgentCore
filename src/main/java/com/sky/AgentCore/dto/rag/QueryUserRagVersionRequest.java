package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.tool.Page;

public class QueryUserRagVersionRequest extends Page {

    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
