package com.sky.AgentCore.service.gateway;

import com.sky.AgentCore.dto.gateway.ApiInstanceDTO;
import com.sky.AgentCore.dto.gateway.ReportResultRequest;
import com.sky.AgentCore.dto.gateway.SelectInstanceRequest;

public interface HighAvailabilityGateway {
    ApiInstanceDTO selectBestInstance(SelectInstanceRequest request);

    void reportResult(ReportResultRequest request);
}
