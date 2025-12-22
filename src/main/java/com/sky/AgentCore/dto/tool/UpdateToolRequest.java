package com.sky.AgentCore.dto.tool;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/** 创建工具的请求对象 */
@Data
public class UpdateToolRequest {

    /** 工具名称 */
    @NotBlank(message = "工具名称不可为空")
    private String name;

    /** 工具图标 */
    private String icon;

    /** 副标题 */
    @NotBlank(message = "副标题不可为空")
    private String subtitle;

    /** 工具描述 */
    @NotBlank(message = "工具描述不可为空")
    private String description;

    /** 标签 */
    @NotEmpty(message = "标签不可为空")
    private List<String> labels;

    /** 上传地址 */
    @NotEmpty(message = "上传地址不可为空")
    private String uploadUrl;

    /** 安装命令 */

    @NotNull(message = "安装命令不可为空")
    private Map<String, Object> installCommand;

    /** 是否为全局工具 */
    private Boolean isGlobal;
}
