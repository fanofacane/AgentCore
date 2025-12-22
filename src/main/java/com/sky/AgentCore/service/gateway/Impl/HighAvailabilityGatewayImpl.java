package com.sky.AgentCore.service.gateway.Impl;

import com.sky.AgentCore.dto.gateway.*;
import com.sky.AgentCore.service.gateway.HighAvailabilityGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighAvailabilityGatewayImpl implements HighAvailabilityGateway {
    @Autowired
    private HighAvailabilityGatewayClient gatewayClient;
    @Override
    public ApiInstanceDTO selectBestInstance(SelectInstanceRequest request) {
        return gatewayClient.selectBestInstance(request);
    }

    @Override
    public void reportResult(ReportResultRequest request) {
        gatewayClient.reportResult(request);
    }

    @Override
    public void createProject(ProjectCreateRequest request) {
        gatewayClient.createProject(request);
    }

    @Override
    public void batchCreateApiInstances(List<ApiInstanceCreateRequest> requests) {
        gatewayClient.batchCreateApiInstances(requests);
    }

    @Override
    public void createApiInstance(ApiInstanceCreateRequest request) {
        gatewayClient.createApiInstance(request);
    }
    @Override
    public void deleteApiInstance(String type, String businessId) {
        gatewayClient.deleteApiInstance(type, businessId);
    }

    @Override
    public void updateApiInstance(String type, String businessId, ApiInstanceUpdateRequest request) {
        gatewayClient.updateApiInstance(type, businessId, request);
    }
    @Override
    public void activateApiInstance(String type, String businessId) {
        gatewayClient.activateApiInstance(type, businessId);
    }

    @Override
    public void deactivateApiInstance(String type, String businessId) {
        gatewayClient.deactivateApiInstance(type, businessId);
    }
}
