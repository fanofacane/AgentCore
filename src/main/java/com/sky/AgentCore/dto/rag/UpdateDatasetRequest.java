package com.sky.AgentCore.dto.rag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateDatasetRequest {

    /** 数据集名称 */
    @NotBlank(message = "数据集名称不能为空")
    @Size(max = 100, message = "数据集名称不能超过100个字符")
    private String name;

    /** 数据集图标 */
    @Size(max = 500, message = "图标URL不能超过500个字符")
    private String icon;

    /** 数据集说明 */
    @Size(max = 1000, message = "数据集说明不能超过1000个字符")
    private String description;
}
