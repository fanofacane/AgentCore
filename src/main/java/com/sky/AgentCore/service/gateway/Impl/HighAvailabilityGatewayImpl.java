package com.sky.AgentCore.service.gateway.Impl;

import com.sky.AgentCore.dto.gateway.ApiInstanceDTO;
import com.sky.AgentCore.dto.gateway.ReportResultRequest;
import com.sky.AgentCore.dto.gateway.SelectInstanceRequest;
import com.sky.AgentCore.service.gateway.HighAvailabilityGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
