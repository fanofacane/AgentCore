package com.sky.AgentCore.dto.rag;

import com.sky.AgentCore.dto.tool.Page;
import lombok.Data;

@Data
public class QueryUserInstalledRagRequest extends Page {

    private String keyword;
}
