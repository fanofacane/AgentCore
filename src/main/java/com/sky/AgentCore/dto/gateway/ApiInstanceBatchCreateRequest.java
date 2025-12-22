package com.sky.AgentCore.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** API实例批量创建请求
 *
 * @author fanofacane
 * @since 1.0.0 */
@Data
@AllArgsConstructor
public class ApiInstanceBatchCreateRequest {

    /** 批量创建的API实例列表 */
    private List<ApiInstanceCreateRequest> instances;

}
