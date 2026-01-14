## 需求确认（按你的描述）
- 不再按 `agent_id + publish_status + MAX(published_at)` 去“找最新版本”。
- 改为：**直接使用 `agents.published_version`（版本ID）去 `agent_versions.id` 查到对应版本记录**，并用该版本记录来组装工作区返回的 `AgentDTO`。

## 现状定位（为什么现在会查“最新版本”）
- 工作区接口入口：[PortalWorkspaceController#getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/controller/agent/PortalWorkspaceController.java#L21-L27)
- 实际组装逻辑在：[AgentWorkspaceServiceImpl#getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L65-L168)
  - 目前会调用 [AgentVersionMapper#selectLatestVersionsByAgentIdsAndStatus](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/mapper/agent/AgentVersionMapper.java#L38-L63) 去取每个 agent 的“最新发布时间版本”。

## 关键字段（你要用的“agent表版本字段”）
- `AgentEntity.publishedVersion` 对应列 `agents.published_version`（含义：当前发布版本的版本ID，指向 `agent_versions.id`）：
  - [AgentEntity.java:L55-L57](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/dto/agent/AgentEntity.java#L55-L57)

## 具体修改方案（只改工作区 getAgents）
### 1）删除“按最新版本查询”的整段逻辑
- 删除/移除以下逻辑块：
  - `bestVersionMap`、`selectLatestVersionsByAgentIdsAndStatus(PUBLISHED/REMOVED)`、`missingAgentIds` 相关代码
  - 范围大致在 [AgentWorkspaceServiceImpl.java:L91-L147](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L91-L147)

### 2）改为批量按 publishedVersion（版本ID）查版本表
- 在查完 `agentEntities` 后：
  - 收集 `publishedVersionIds = agentEntities.map(AgentEntity::getPublishedVersion)`（过滤空、去重）
  - 调用 `agentVersionMapper.selectBatchIds(publishedVersionIds)` 批量取 `AgentVersionEntity`
  - 构建 `versionByIdMap: versionId -> AgentVersionEntity`

### 3）组装 AgentDTO 的新优先级
- 遍历工作区 `agentIds` 保持原顺序：
  1. 取 `AgentEntity agentEntity = agentEntityMap.get(agentId)`，为空则跳过（与现有行为一致）
  2. `String publishedVersion = agentEntity.getPublishedVersion()`
  3. 若 `publishedVersion` 不为空且 `versionByIdMap` 命中：
     - `BeanUtils.copyProperties(agentVersionEntity, dto)`
     - `dto.setId(agentId)`（避免 dto.id 被版本记录 id 覆盖）
     - `dto.setPublishedVersion(publishedVersion)`
  4. 否则（版本ID为空或查不到版本记录）：
     - `BeanUtils.copyProperties(agentEntity, dto)`
     - `dto.setId(agentId)`
     - `dto.setPublishedVersion(publishedVersion)`
  5. 统一覆盖 `enabled`：`dto.setEnabled(agentEnabledMap.getOrDefault(agentId, false))`

### 4）安全性/一致性增强（可选但建议）
- 若查到的 `AgentVersionEntity.agentId` 与当前 `agentId` 不一致，则不使用该版本，直接回退到 `agentEntity`（避免脏数据导致串号）。

## 验证方式（改完立即验证）
- 本地编译/测试：运行 Maven 测试（`mvn test`）确保编译通过。
- 手工接口验证：调用 `/agents/workspaces/agents`，对比返回的 `publishedVersion` 与 `agents.published_version` 是否一致。

如果你确认这个方案，我会按上述步骤直接改 [AgentWorkspaceServiceImpl#getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L65-L168) 并完成编译验证。