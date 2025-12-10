package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
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
import com.sky.AgentCore.service.agent.AgentAppService;
import com.sky.AgentCore.service.agent.AgentVersionService;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.billing.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static cn.hutool.core.io.FileUtil.exist;


@Service
public class AgentAppServiceImpl extends ServiceImpl<AgentMapper, AgentEntity> implements AgentAppService {
    private static final Logger logger = LoggerFactory.getLogger(AgentAppService.class);
    @Autowired
    private BillingService billingService;
    @Autowired
    private AgentWorkspaceService agentWorkspaceService;
    @Autowired
    private AgentVersionService agentVersionService;
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

    @Override
    public AgentDTO updateAgent(UpdateAgentRequest request, String userId) {
        // 使用组装器创建更新实体
        AgentEntity updateEntity = AgentAssembler.toEntity(request, userId);

        // 调用领域服务更新Agent
        boolean success = lambdaUpdate().eq(AgentEntity::getId, updateEntity.getId()).eq(AgentEntity::getUserId, userId).update(updateEntity);
        if (!success) {
            throw new BusinessException("Agent更新失败，数据不存在或无操作权限");
        }
        return AgentAssembler.toDTO(updateEntity);
    }

    @Override
    public AgentDTO toggleAgentStatus(String agentId) {
        AgentEntity agent = getById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在:"+agentId);
        }
        if (Boolean.TRUE==agent.getEnabled()) {
            agent.disable();
        } else {
            agent.enable();
        }
        boolean success = updateById(agent);
        if (!success) throw new BusinessException("Agent状态更新失败");

        return AgentAssembler.toDTO(agent);
    }

    @Override
    @Transactional
    public void deleteAgent(String agentId, String userId) {
        // 先删除Agent关联的定时任务（包括取消延迟队列中的任务）TODO
        //scheduledTaskExecutionService.deleteTasksByAgentId(agentId, userId);
        // 再删除Agent本身
        boolean success = remove(new LambdaQueryWrapper<AgentEntity>().eq(AgentEntity::getId, agentId).eq(AgentEntity::getUserId, userId));
        if (!success) throw new BusinessException("Agent删除失败");
        // 最后删除agent版本  TODO

    }

    @Override
    public AgentEntity getAgentWithPermissionCheck(String agentId, String userId) {
        // 检查工作区是否存在
        boolean b1 = agentWorkspaceService.lambdaQuery().eq(AgentWorkspaceEntity::getAgentId, agentId)
                .eq(AgentWorkspaceEntity::getUserId, userId).exists();

        boolean b2 = exist(agentId, userId);
        if (!b1 && !b2) throw new BusinessException("助理不存在");

        AgentEntity agentEntity = lambdaQuery().eq(AgentEntity::getId, agentId).one();

        // 如果有版本则使用版本
        String publishedVersion = agentEntity.getPublishedVersion();
        if (StringUtils.hasText(publishedVersion)) {
            System.out.println("有版本");
            AgentVersionEntity agentVersionEntity = agentVersionService.getById(publishedVersion);
            BeanUtils.copyProperties(agentVersionEntity, agentEntity);
        }

        return agentEntity;
    }

    @Override
    public AgentEntity getAgentById(String agentId) {
        return getById(agentId);
    }

    @Override
    public AgentVersionEntity getLatestAgentVersion(String agentId) {
        AgentVersionEntity version = agentVersionService.lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .orderByDesc(AgentVersionEntity::getPublishedAt)
                .last("LIMIT 1").one();
        if (version == null) return null;
        return version;
    }


    /** 校验 agent 是否存在 */
    public boolean exist(String agentId, String userId) {

        AgentEntity agent = lambdaQuery()
                .eq(AgentEntity::getId, agentId)
                .eq(AgentEntity::getUserId, userId).one();
        return agent != null;
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
