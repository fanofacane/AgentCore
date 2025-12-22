package com.sky.AgentCore.service.gateway;

import com.sky.AgentCore.dto.gateway.*;

import java.util.List;

public interface HighAvailabilityGateway {

    /** 选择最佳实例
     *
     * @param request 选择实例请求
     * @return 选择的实例信息 */
    ApiInstanceDTO selectBestInstance(SelectInstanceRequest request);

    /** 创建API实例
     *
     * @param request 创建实例请求 */
    void createApiInstance(ApiInstanceCreateRequest request);

    /** 删除API实例
     *
     * @param type 实例类型
     * @param businessId 业务ID */
    void deleteApiInstance(String type, String businessId);

    /** 更新API实例
     *
     * @param type 实例类型
     * @param businessId 业务ID
     * @param request 更新请求 */
    void updateApiInstance(String type, String businessId, ApiInstanceUpdateRequest request);

    /** 上报调用结果
     *
     * @param request 结果上报请求 */
    void reportResult(ReportResultRequest request);

    /** 创建项目
     *
     * @param request 项目创建请求 */
    void createProject(ProjectCreateRequest request);

    /** 批量创建API实例
     *
     * @param requests 批量创建请求列表 */
    void batchCreateApiInstances(List<ApiInstanceCreateRequest> requests);

    /** 激活API实例
     *
     * @param type 实例类型
     * @param businessId 业务ID */
    void activateApiInstance(String type, String businessId);

    /** 停用API实例
     *
     * @param type 实例类型
     * @param businessId 业务ID */
    void deactivateApiInstance(String type, String businessId);

}
