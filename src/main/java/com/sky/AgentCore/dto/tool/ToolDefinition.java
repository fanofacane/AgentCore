package com.sky.AgentCore.dto.tool;


import lombok.Data;

import java.util.Map;

/** 工具定义 */
@Data
public class ToolDefinition {
    /** 工具名称 */
    private String name;

    /** 工具描述 */
    private String description;

    /** 参数定义 */
    private Map<String, Object> parameters;

    /** 是否启用 */
    private Boolean enabled;
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    <function>\n");
        sb.append("      <name>").append(name).append("</name>\n");
        sb.append("      <description>").append(description).append("</description>\n");

        if (parameters != null && parameters.containsKey("properties")) {
            Object propsObject = parameters.get("properties");
            if (propsObject instanceof Map) {
                Map<String, Object> properties = (Map<String, Object>) propsObject;
                if (!properties.isEmpty()) {
                    sb.append("      <parameters>\n");
                    properties.forEach((paramName, paramDetails) -> {
                        if (paramDetails instanceof Map) {
                            Map<String, String> detailsMap = (Map<String, String>) paramDetails;
                            sb.append("        <parameter>\n");
                            sb.append("          <name>").append(paramName).append("</name>\n");
                            sb.append("          <type>").append(detailsMap.get("type")).append("</type>\n");
                            sb.append("          <description>").append(detailsMap.get("description"))
                                    .append("</description>\n");
                            sb.append("        </parameter>\n");
                        }
                    });
                    sb.append("      </parameters>\n");
                }
            }
        }

        sb.append("    </function>\n");
        return sb.toString();
    }
}

