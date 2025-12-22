package com.sky.AgentCore.service.gateway.Impl;

import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.config.HighAvailabilityProperties;
import com.sky.AgentCore.dto.gateway.ApiInstanceDTO;
import com.sky.AgentCore.dto.gateway.GatewayResult;
import com.sky.AgentCore.dto.gateway.ReportResultRequest;
import com.sky.AgentCore.dto.gateway.SelectInstanceRequest;
import com.sky.AgentCore.utils.JsonUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/** 高可用网关HTTP客户端 负责与高可用网关进行HTTP通信
 * @author fanofacane
 * @since 1.0.0 */
@Component
public class HighAvailabilityGatewayClient {
    private static final Logger logger = LoggerFactory.getLogger(HighAvailabilityGatewayClient.class);
    @Autowired
    private HighAvailabilityProperties properties;
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    /** 选择最佳API实例 */
    public ApiInstanceDTO selectBestInstance(SelectInstanceRequest request) {
        if (!properties.isEnabled()) {
            throw new BusinessException("高可用功能未启用");
        }

        try {
            String url = properties.getGatewayUrl() + "/gateway/select-instance";

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("api-key", properties.getApiKey());

            String jsonRequest = JsonUtils.toJsonString(request);
            httpPost.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                if (response.getStatusLine().getStatusCode() != 200) {
                    logger.error("选择实例失败，响应码: {}, 响应体: {}", response.getStatusLine().getStatusCode(), responseBody);
                    throw new BusinessException("选择实例失败: " + responseBody);
                }

                // 在客户端层解析响应
                GatewayResult<?> rawResult = JsonUtils.parseObject(responseBody, GatewayResult.class);

                if (rawResult == null || !rawResult.isSuccess() || rawResult.getData() == null) {
                    String errorMsg = rawResult != null ? rawResult.getMessage() : "解析响应失败";
                    logger.error("网关返回失败: {}", errorMsg);
                    throw new BusinessException("网关返回失败: " + errorMsg);
                }

                // 将data部分转换为ApiInstanceDTO
                String dataJson = JsonUtils.toJsonString(rawResult.getData());
                ApiInstanceDTO selectedInstance = JsonUtils.parseObject(dataJson, ApiInstanceDTO.class);

                if (selectedInstance == null) {
                    logger.error("解析API实例信息失败");
                    throw new BusinessException("解析API实例信息失败");
                }

                logger.info("成功选择实例: businessId={}, instanceId={}", selectedInstance.getBusinessId(),
                        selectedInstance.getId());
                return selectedInstance;
            }

        } catch (Exception e) {
            logger.error("选择API实例失败", e);
            throw new BusinessException("选择API实例失败", e);
        }
    }

    public void reportResult(ReportResultRequest request) {
        try {
            String url = properties.getGatewayUrl() + "/gateway/report-result";

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("api-key", properties.getApiKey());

            String jsonRequest = JsonUtils.toJsonString(request);
            httpPost.setEntity(new StringEntity(jsonRequest, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    logger.warn("上报调用结果失败，响应码: {}, 响应体: {}", response.getStatusLine().getStatusCode(), responseBody);
                }
            }

        } catch (Exception e) {
            // 上报失败不抛异常，避免影响主流程
            logger.error("上报调用结果失败", e);
        }
    }
}
