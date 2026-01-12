## 问题原因（为什么创建后看不到）
- 创建Agent时确实写入了 `agent_workspace`：`AgentAppServiceImpl.createAgent` 里调用了 `agentWorkspaceService.save(...)`。
- 但工作区查询 `AgentWorkspaceServiceImpl.getAgents` 当前只用 `agent_versions` 组装返回结果：
  - 只查 `publish_status in (PUBLISHED, REMOVED)` 的版本。
  - 新创建未发布的Agent没有任何 `agent_versions` 记录，所以最终返回列表不包含它。

## 修复目标
- 用户工作区里只要存在 `agent_workspace` 记录，就能看到该Agent：
  - 有版本：优先用版本信息展示（PUBLISHED 优先，否则 REMOVED）。
  - 没有版本：回退使用 `agents` 表信息展示（用于“刚创建未发布”的场景）。
- 保持现有约束：其他用户仍无法安装未发布Agent（`addAgent` 的发布校验不变）。

## 实现步骤
1. 修改 [AgentWorkspaceServiceImpl.getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L63-L123)：
   - 预先计算每个 agentId 的“最佳版本”（PUBLISHED 优先，否则 REMOVED）。
   - 按 `agent_workspace` 返回的 `agentIds` 顺序逐个组装 `AgentDTO`：
     - 若存在最佳版本：用 `AgentVersionEntity` copyProperties 到 DTO，并 `dto.id = agentId`。
     - 否则：用 `AgentEntity` copyProperties 到 DTO，并 `dto.id = agentId`。
   - `dto.enabled`：优先取 `agents.enabled`（若查不到则默认 `false`）。
2. 加一个小的健壮性处理：如果 agentId 既无版本又查不到 agent 记录（极端残留 workspace 关系），则跳过该条。

## 验证方式
- 本地编译通过（mvn -DskipTests compile）。
- 逻辑自测场景：创建Agent但不发布 → 调用工作区列表接口 → 能看到新Agent；发布后 → 仍能看到且展示版本字段。

我将按上述步骤修改并做编译验证。