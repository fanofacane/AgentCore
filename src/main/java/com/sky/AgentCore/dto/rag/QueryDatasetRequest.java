package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.tool.Page;

/** 数据集查询请求*/
public class QueryDatasetRequest extends Page {

    /** 搜索关键词 */
    private String keyword;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
