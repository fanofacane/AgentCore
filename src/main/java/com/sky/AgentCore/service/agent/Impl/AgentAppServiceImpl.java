package com.sky.AgentCore.service.agent.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.Exceptions.InsufficientBalanceException;
import com.sky.AgentCore.Exceptions.ParamValidationException;
import com.sky.AgentCore.constant.UsageDataKeys;
import com.sky.AgentCore.converter.AgentAssembler;
import com.sky.AgentCore.converter.AgentVersionAssembler;
import com.sky.AgentCore.dto.LLMModelConfig;
import com.sky.AgentCore.dto.agent.*;
import com.sky.AgentCore.dto.billing.RuleContext;
import com.sky.AgentCore.dto.rag.RagVersionEntity;
import com.sky.AgentCore.dto.rag.UserRagEntity;
import com.sky.AgentCore.dto.session.SessionEntity;
import com.sky.AgentCore.dto.tool.UserToolEntity;
import com.sky.AgentCore.enums.BillingType;
import com.sky.AgentCore.enums.PublishStatus;
import com.sky.AgentCore.enums.RagPublishStatus;
import com.sky.AgentCore.mapper.AgentMapper;
import com.sky.AgentCore.mapper.SessionMapper;
import com.sky.AgentCore.service.agent.AgentAppService;
import com.sky.AgentCore.service.agent.AgentSessionService;
import com.sky.AgentCore.service.agent.AgentVersionService;
import com.sky.AgentCore.service.agent.AgentWorkspaceService;
import com.sky.AgentCore.service.billing.BillingService;
import com.sky.AgentCore.service.rag.RagVersionService;
import com.sky.AgentCore.service.rag.UserRagService;
import com.sky.AgentCore.service.tool.ToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AgentAppServiceImpl extends ServiceImpl<AgentMapper, AgentEntity> implements AgentAppService {
    private static final Logger logger = LoggerFactory.getLogger(AgentAppService.class);
    @Autowired
    private BillingService billingService;
    @Autowired
    private AgentWorkspaceService agentWorkspaceService;
    @Autowired
    private AgentVersionService agentVersionService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private RagVersionService ragVersionService;
    @Autowired
    private UserRagService userRagService;
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
        return AgentAssembler.toDTOs(agentList);
    }

    @Override
    public AgentDTO getAgent(String agentId, String userId) {
        AgentEntity agent = lambdaQuery().eq(AgentEntity::getId, agentId).eq(AgentEntity::getUserId, userId).one();
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

    @Override
    public List<AgentVersionDTO> getPublishedAgentsByName(SearchAgentsRequest searchAgentsRequest, String userId) {
        AgentEntity entity = AgentAssembler.toEntity(searchAgentsRequest);
        List<AgentVersionEntity> agentVersionEntities = agentVersionService.getPublishedAgentsByName(entity);
        if (agentVersionEntities.isEmpty()) return new ArrayList<>();

        List<String> agentIds = agentVersionEntities.stream().map(AgentVersionEntity::getAgentId).toList();
        List<AgentWorkspaceEntity> agentWorkspaceEntities = agentWorkspaceService.listAgents(agentIds, userId);
        Set<String> agentIdsSet = agentWorkspaceEntities.stream().map(AgentWorkspaceEntity::getAgentId)
                .collect(Collectors.toSet());

        List<AgentVersionDTO> agentVersionDTOS = AgentVersionAssembler.toDTOs(agentVersionEntities);
        if (agentIdsSet.isEmpty()) {
            return agentVersionDTOS;
        }
        for (AgentVersionDTO agentVersionDTO : agentVersionDTOS) {
            agentVersionDTO.setIsAddWorkspace(agentIdsSet.contains(agentVersionDTO.getAgentId()));
        }
        return agentVersionDTOS;
    }

    @Override
    public AgentVersionDTO publishAgentVersion(String agentId, PublishAgentVersionRequest request, String userId) {
        // 在应用层验证请求
        request.validate();

        // 获取当前Agent
        AgentEntity agent = lambdaQuery().eq(AgentEntity::getId, agentId).eq(AgentEntity::getUserId, userId).one();
        if (agent == null) throw new BusinessException("Agent不存在"+agentId);
        // 获取最新版本，检查版本号大小
        AgentVersionEntity agentVersionEntity = agentVersionService.getLatestAgentVersion(agentId);
        if (agentVersionEntity != null) {
            // 检查版本号是否大于上一个版本
            if (!request.isVersionGreaterThan(agentVersionEntity.getVersionNumber())) {
                throw new ParamValidationException("versionNumber", "新版本号(" + request.getVersionNumber()
                        + ")必须大于当前最新版本号(" + agentVersionEntity.getVersionNumber() + ")");
            }
        }

        // 使用组装器创建版本实体
        AgentVersionEntity versionEntity = AgentVersionAssembler.createVersionEntity(agent, request);

        versionEntity.setUserId(userId);

        // 验证Agent依赖的工具和知识库权限
        validateAgentDependencies(versionEntity, userId);

        // 调用领域服务发布版本
        agentVersionEntity = publishAgentVersion(agentId, versionEntity);
        return AgentVersionAssembler.toDTO(agentVersionEntity);
    }

    @Override
    public AgentVersionDTO getAgentVersion(String agentId, String versionNumber) {
        AgentVersionEntity agentVersion = agentVersionService.lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getVersionNumber, versionNumber).one();
        return AgentVersionAssembler.toDTO(agentVersion);
    }

    @Override
    public List<AgentVersionDTO> getAgentVersions(String agentId, String userId) {
        AgentEntity agent = getById(agentId);
        if (agent==null) throw new BusinessException("Agent不存在");

        // 如果userId不为空，需要检查权限（普通用户只能访问自己的Agent版本）
        // 如果userId为空，表示管理员访问，跳过权限检查
        if (userId != null && !agent.getUserId().equals(userId)) throw new BusinessException("无权访问");

        List<AgentVersionEntity> agentVersionEntities = agentVersionService.lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .orderByDesc(AgentVersionEntity::getPublishedAt)
                .list();
        return AgentVersionAssembler.toDTOs(agentVersionEntities);
    }

    /** 发布Agent版本 */
    public AgentVersionEntity publishAgentVersion(String agentId, AgentVersionEntity versionEntity) {
        AgentEntity agent = getById(agentId);
        if (agent == null) throw new BusinessException("Agent不存在: " + agentId);

        // 查询最新版本号进行比较
        AgentVersionEntity latestVersion = agentVersionService.lambdaQuery()
                .eq(AgentVersionEntity::getAgentId, agentId)
                .eq(AgentVersionEntity::getUserId, agent.getUserId())
                .orderByDesc(AgentVersionEntity::getPublishedAt).last("LIMIT 1").one();

        if (latestVersion != null) {
            // 版本号比较
            String newVersion = versionEntity.getVersionNumber();
            String oldVersion = latestVersion.getVersionNumber();

            // 检查是否为相同版本号
            if (newVersion.equals(oldVersion)) {
                throw new BusinessException("版本号已存在: " + newVersion);
            }

            // 检查新版本号是否大于旧版本号
            if (!isVersionGreaterThan(newVersion, oldVersion)) {
                throw new BusinessException("新版本号(" + newVersion + ")必须大于当前最新版本号(" + oldVersion + ")");
            }
        }

        // 设置版本关联的Agent ID
        versionEntity.setAgentId(agentId);

        // todo 设置版本状态为审核中
        versionEntity.setPublishStatus(PublishStatus.PUBLISHED.getCode());

        // 保存版本
        agentVersionService.save(versionEntity);
        lambdaUpdate().eq(AgentEntity::getId, agentId).set(AgentEntity::getPublishedVersion, versionEntity.getId()).update();
        return versionEntity;
    }

    /** 比较版本号大小
     *
     * @param newVersion 新版本号
     * @param oldVersion 旧版本号
     * @return 如果新版本大于旧版本返回true，否则返回false */
    private boolean isVersionGreaterThan(String newVersion, String oldVersion) {
        if (oldVersion == null || oldVersion.trim().isEmpty()) {
            return true; // 如果没有旧版本，新版本肯定更大
        }

        // 分割版本号
        String[] current = newVersion.split("\\.");
        String[] last = oldVersion.split("\\.");

        // 确保版本号格式正确
        if (current.length != 3 || last.length != 3) {
            throw new BusinessException("版本号必须遵循 x.y.z 格式");
        }

        try {
            // 比较主版本号
            int currentMajor = Integer.parseInt(current[0]);
            int lastMajor = Integer.parseInt(last[0]);
            if (currentMajor > lastMajor)
                return true;
            if (currentMajor < lastMajor)
                return false;

            // 主版本号相同，比较次版本号
            int currentMinor = Integer.parseInt(current[1]);
            int lastMinor = Integer.parseInt(last[1]);
            if (currentMinor > lastMinor)
                return true;
            if (currentMinor < lastMinor)
                return false;

            // 主版本号和次版本号都相同，比较修订版本号
            int currentPatch = Integer.parseInt(current[2]);
            int lastPatch = Integer.parseInt(last[2]);

            return currentPatch > lastPatch;
        } catch (NumberFormatException e) {
            throw new BusinessException("版本号格式错误，必须是数字: " + e.getMessage());
        }
    }

    /** 验证Agent发布时依赖的工具和知识库权限
     *
     * @param versionEntity Agent版本实体
     * @param userId 当前用户ID
     * @throws BusinessException 当权限验证失败时抛出异常 */
    private void validateAgentDependencies(AgentVersionEntity versionEntity, String userId) {
        // 验证工具权限
        if (versionEntity.getToolIds() != null && !versionEntity.getToolIds().isEmpty()) {
            for (String toolId : versionEntity.getToolIds()) {
                validateToolPermission(toolId, userId);
            }
        }

        // 验证知识库权限
        if (versionEntity.getKnowledgeBaseIds() != null && !versionEntity.getKnowledgeBaseIds().isEmpty()) {
            for (String knowledgeBaseId : versionEntity.getKnowledgeBaseIds()) {
                validateKnowledgeBasePermission(knowledgeBaseId, userId);
            }
        }
    }

    /** 验证知识库权限
     *
     * @param knowledgeBaseId 知识库ID
     * @param userId 用户ID
     * @throws BusinessException 当用户未安装该知识库或知识库版本未发布时抛出异常 */
    private void validateKnowledgeBasePermission(String knowledgeBaseId, String userId) {
        // 先尝试获取知识库信息用于友好的错误提示
        String knowledgeBaseName = getKnowledgeBaseDisplayName(knowledgeBaseId,userId);

        // 检查用户是否安装了该知识库
        if (!userRagService.isRagInstalledByOriginalId(userId, knowledgeBaseId)) {
            throw new BusinessException("您尚未安装知识库「" + knowledgeBaseName + "」，无法发布使用该知识库的Agent");
        }

        // 获取用户安装的知识库信息
        UserRagEntity userRag = userRagService.findInstalledRagByOriginalId(userId, knowledgeBaseId);
        if (userRag == null) {
            throw new BusinessException("知识库「" + knowledgeBaseName + "」未找到安装记录");
        }

        // 获取对应的RAG版本信息来检查创建者和发布状态
        RagVersionEntity ragVersion = ragVersionService.getRagVersion(userRag.getRagVersionId());

        // 如果不是自己创建的知识库，需要检查版本是否已发布
        if (!userId.equals(ragVersion.getUserId())) {
            // 对于非创建者，需要确保使用的是已发布的版本
            if (!RagPublishStatus.PUBLISHED.getCode().equals(ragVersion.getPublishStatus())) {
                throw new BusinessException(
                        "知识库「" + ragVersion.getName() + " v" + ragVersion.getVersion() + "」的版本未发布，无法发布使用该知识库的Agent");
            }
        }
        // 创建者可以使用自己的任何版本，包括未发布的版本
    }

    /** 获取知识库显示名称（用于错误提示）
     *
     * @param knowledgeBaseId 知识库ID
     * @return 知识库显示名称 */
    private String getKnowledgeBaseDisplayName(String knowledgeBaseId,String userId) {
        try {
            // 尝试获取任意一个版本来获取知识库名称
            List<RagVersionEntity> versions = ragVersionService.getVersionsByOriginalRagId(knowledgeBaseId, userId);
            if (!versions.isEmpty()) {
                RagVersionEntity firstVersion = versions.getFirst();
                return firstVersion.getName() + " v" + firstVersion.getVersion();
            }
        } catch (Exception e) {
            // 如果获取失败，返回ID的前8位作为友好显示
        }
        return knowledgeBaseId.length() > 8 ? knowledgeBaseId.substring(0, 8) + "..." : knowledgeBaseId;
    }

    /** 验证工具权限
     *
     * @param toolId 工具ID
     * @param userId 用户ID
     * @throws BusinessException 当用户未安装该工具或工具版本未公开时抛出异常 */
    private void validateToolPermission(String toolId, String userId) {
        UserToolEntity userTool = toolService.findByToolIdAndUserId(toolId, userId);
        if (userTool == null) {
            // 尝试获取工具名称用于友好提示
            String toolName = getToolDisplayName(toolId);
            throw new BusinessException("您尚未安装工具「" + toolName + "」，无法发布使用该工具的Agent");
        }

        // 检查用户安装的工具版本是否为公开版本（创建者可以使用私有版本）
        if (!userId.equals(userTool.getUserId()) && !Boolean.TRUE.equals(userTool.getPublicState())) {
            throw new BusinessException(
                    "工具「" + userTool.getName() + " v" + userTool.getVersion() + "」的版本未公开，无法发布使用该工具的Agent");
        }
    }

    /** 获取工具显示名称（用于错误提示）
     *
     * @param toolId 工具ID
     * @return 工具显示名称 */
    private String getToolDisplayName(String toolId) {
        try {
            // 这里可以尝试从工具服务获取工具名称，但为了避免循环依赖，暂时返回简化ID
            return toolId.length() > 8 ? toolId.substring(0, 8) + "..." : toolId;
        } catch (Exception e) {
            return toolId.length() > 8 ? toolId.substring(0, 8) + "..." : toolId;
        }
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
