package com.sky.AgentCore.service.gateway.Impl;

import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.constant.AffinityType;
import com.sky.AgentCore.dto.config.HighAvailabilityProperties;
import com.sky.AgentCore.dto.gateway.ApiInstanceDTO;
import com.sky.AgentCore.dto.gateway.HighAvailabilityResult;
import com.sky.AgentCore.dto.gateway.ReportResultRequest;
import com.sky.AgentCore.dto.gateway.SelectInstanceRequest;
import com.sky.AgentCore.dto.model.*;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.gateway.HighAvailabilityGateway;
import com.sky.AgentCore.service.gateway.HighAvailabilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HighAvailabilityServiceImpl implements HighAvailabilityService {
    private static final Logger logger = LoggerFactory.getLogger(HighAvailabilityServiceImpl.class);
    @Autowired
    private HighAvailabilityProperties properties;
    @Autowired
    private HighAvailabilityGateway gateway;
    @Autowired
    private LLMDomainService llmDomainService;

    @Override
    public void syncModelToGateway(ModelEntity model) {

    }

    @Override
    public void removeModelFromGateway(String modelId, String userId) {

    }

    @Override
    public void updateModelInGateway(ModelEntity model) {

    }

    @Override
    public HighAvailabilityResult selectBestProvider(ModelEntity model, String userId) {
        return null;
    }

    @Override
    public HighAvailabilityResult selectBestProvider(ModelEntity model, String userId, String sessionId) {
        return null;
    }

    @Override
    public HighAvailabilityResult selectBestProvider(ModelEntity model, String userId, String sessionId, List<String> fallbackChain) {
        if (!properties.isEnabled()) {
            // 高可用未启用，使用默认逻辑
            logger.debug("高可用功能未启用，使用默认Provider选择逻辑: modelId={}", model.getId());
            ProviderEntity provider = llmDomainService.getProvider(model.getProviderId());
            return new HighAvailabilityResult(provider, model, null, false);
        }

        try {
            // 构建选择实例请求
            SelectInstanceRequest request = new SelectInstanceRequest(userId, model.getModelId(), "MODEL");

            // 如果提供了sessionId，则设置会话亲和性
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                request.setAffinityKey(sessionId);
                request.setAffinityType(AffinityType.SESSION);
                logger.debug("启用会话亲和性: sessionId={}, modelId={}", sessionId, model.getId());
            }

            // 设置降级链（从参数传入，而不是内部获取）
            if (fallbackChain != null && !fallbackChain.isEmpty()) {
                request.setFallbackChain(fallbackChain);
                logger.debug("启用降级链: userId={}, primaryModel={}, fallbackModels={}", userId, model.getModelId(),
                        fallbackChain);
            }

            // 通过高可用网关选择最佳实例
            ApiInstanceDTO selectedInstance = gateway.selectBestInstance(request);

            String businessId = selectedInstance.getBusinessId();
            String instanceId = selectedInstance.getId();

            // 获取最佳实例对应的模型
            ModelEntity bestModel = llmDomainService.selectModelById(businessId);

            // 判断模型是否被切换（通过比较主键id）
            boolean switched = !model.getId().equals(bestModel.getId());

            // 返回最佳模型对应的Provider
            ProviderEntity provider = llmDomainService.getProvider(bestModel.getProviderId());

            logger.info("通过高可用网关选择Provider成功: modelId={}, bestBusinessId={}, providerId={}, sessionId={}, switched={}",
                    model.getId(), businessId, provider.getId(), sessionId, switched);

            return new HighAvailabilityResult(provider, bestModel, instanceId, switched);

        } catch (Exception e) {
            logger.warn("高可用网关选择Provider失败，降级到默认逻辑: modelId={}, sessionId={}", model.getId(), sessionId, e);

            // 降级处理：使用默认逻辑
            try {
                ProviderEntity provider = llmDomainService.getProvider(model.getProviderId());
                return new HighAvailabilityResult(provider, model, null, false);
            } catch (Exception fallbackException) {
                logger.error("降级逻辑也失败了: modelId={}, sessionId={}", model.getId(), sessionId, fallbackException);
                throw new BusinessException("获取Provider失败", fallbackException);
            }
        }
    }
    @Async
    @Override
    public void reportCallResult(String instanceId, String modelId, boolean success, long latencyMs, String errorMessage) {
        if (!properties.isEnabled()) return;
        try {
            ReportResultRequest request = new ReportResultRequest();
            request.setInstanceId(instanceId);
            request.setBusinessId(modelId);
            request.setSuccess(success);
            request.setLatencyMs(latencyMs);
            request.setErrorMessage(errorMessage);
            request.setCallTimestamp(System.currentTimeMillis());

            gateway.reportResult(request);

            logger.debug("成功上报调用结果: instanceId={}, modelId={}, success={}, latency={}ms", instanceId, modelId, success, latencyMs);
        } catch (Exception e) {
            logger.error("上报调用结果失败: instanceId={}, modelId={}", instanceId, modelId, e);
        }
    }

    @Override
    public void initializeProject() {

    }

    @Override
    public void syncAllModelsToGateway() {

    }

    @Override
    public void changeModelStatusInGateway(ModelEntity model, boolean enabled, String reason) {

    }

    @Override
    public void batchRemoveModelsFromGateway(List<ModelsBatchDeletedEvent.ModelDeleteItem> deleteItems, String userId) {

    }
}
