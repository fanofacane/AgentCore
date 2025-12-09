package com.sky.AgentCore.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.Exceptions.InsufficientBalanceException;
import com.sky.AgentCore.constant.UsageDataKeys;
import com.sky.AgentCore.converter.AgentAssembler;
import com.sky.AgentCore.dto.LLMModelConfig;
import com.sky.AgentCore.dto.agent.*;
import com.sky.AgentCore.dto.billing.RuleContext;
import com.sky.AgentCore.enums.BillingType;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.service.AgentAppService;
import com.sky.AgentCore.service.AgentWorkspaceService;
import com.sky.AgentCore.service.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;


@Service
public class AgentAppServiceImpl extends ServiceImpl<AgentMapper, AgentEntity> implements AgentAppService {
    private static final Logger logger = LoggerFactory.getLogger(AgentAppService.class);
    @Autowired
    private BillingService billingService;
    @Autowired
    private AgentWorkspaceService agentWorkspaceService;
    /** 创建新Agent */
    @Override
    @Transactional
    public AgentDTO createAgent(CreateAgentRequest request, String userId) {
        logger.info("开始创建Agent - 用户: {}, Agent名称: {}", userId, request.getName());

        // 1. 创建计费上下文进行余额预检查
        RuleContext billingContext = RuleContext.builder().type(BillingType.AGENT_CREATION.getCode())
                .serviceId("agent_creation") // 固定业务标识
                .usageData(Map.of(UsageDataKeys.QUANTITY, 1)).requestId(generateRequestId(userId, "creation"))
                .userId(userId).build();

        // 2. 余额预检查 - 避免创建后发现余额不足
        if (!billingService.checkBalance(billingContext)) {
            logger.warn("Agent创建失败 - 用户余额不足: {}", userId);
            throw new InsufficientBalanceException("账户余额不足，无法创建Agent。请先充值后再试。");
        }

        // 3. 执行Agent创建逻辑
        AgentEntity agent = AgentAssembler.toEntity(request, userId);
        agent.setUserId(userId);
        save(agent);
        AgentWorkspaceEntity agentWorkspaceEntity = new AgentWorkspaceEntity(agent.getId(), userId,
                new LLMModelConfig());
        agentWorkspaceService.save(agentWorkspaceEntity);

        // 4. 创建成功后执行计费扣费
        try {
            billingService.charge(billingContext);
            logger.info("Agent创建及计费成功 - 用户: {}, AgentID: {}, 请求ID: {}", userId, agent.getId(),
                    billingContext.getRequestId());
        } catch (Exception e) {
            // 计费失败但Agent已创建，记录错误日志但不影响用户体验
            // 实际场景中可能需要考虑回滚Agent创建或者重试机制
            logger.error("Agent创建成功但计费失败 - 用户: {}, AgentID: {}, 错误: {}", userId, agent.getId(), e.getMessage(), e);
            throw new InsufficientBalanceException("Agent创建成功，但计费处理失败: " + e.getMessage());
        }

        return AgentAssembler.toDTO(agent);
    }

    @Override
    public List<AgentDTO> getUserAgents(String userId, SearchAgentsRequest searchAgentsRequest) {
        List<AgentEntity> agentList = lambdaQuery().eq(AgentEntity::getUserId, userId)
                .like(!StringUtils.isEmpty(searchAgentsRequest.getName()),AgentEntity::getName, searchAgentsRequest.getName()).list();
        System.out.println("agentlist"+agentList);
        return AgentAssembler.toDTOs(agentList);
    }

    @Override
    public AgentDTO getAgent(String agentId, String userId) {
        AgentEntity agent = lambdaQuery().eq(AgentEntity::getId, agentId).eq(AgentEntity::getUserId, userId).one();
        System.out.println("agentid="+agentId+"userid"+userId);
        if (agent == null)  throw new BusinessException("Agent不存在");
        return AgentAssembler.toDTO(agent);
    }

    /** 生成用于计费的唯一请求ID
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @return 唯一请求ID */
    private String generateRequestId(String userId, String action) {
        return String.format("agent_%s_%s_%d", action, userId, System.currentTimeMillis());
    }
}
