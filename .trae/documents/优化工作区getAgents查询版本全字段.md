## 约束
- `agent_versions` 必须查询全字段（不能做列裁剪）。

## 性能瓶颈（当前慢的根因）
- [AgentWorkspaceServiceImpl.getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L63-L145) 现在会把这些 agentIds 的 **所有版本行**（PUBLISHED/REMOVED）全查出来，再在 Java 里 `groupBy + max`。
- 版本多时，IO/反序列化/内存/GC 才是主要开销（不是 Java 里几次 map）。

## 优化思路（查询全字段但减少“行数”）
- 把“每个 agent 只取最新版本”下推到数据库：
  - **第一条SQL**：每个 agent 取最新 `PUBLISHED` 版本（返回 v.*，每个 agent 最多 1 行）。
  - **第二条SQL**：对没有 `PUBLISHED` 的 agent，再取最新 `REMOVED` 版本（同样 v.*，每个 agent 最多 1 行）。
- 这样即使 `agent_versions` 全字段返回，也从“可能上千行”变成“最多 2 * agent数 行”。

## 具体改动（确认后我会直接改代码）
1) 在 [AgentVersionMapper](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/mapper/agent/AgentVersionMapper.java) 新增 2 个方法（SQL 形态复用你现有的 `selectLatestVersionsByNameAndStatus` 写法）：
- `selectLatestVersionsByAgentIdsAndStatus(List<String> agentIds, Integer status)`
  - 子查询 `GROUP BY agent_id, MAX(published_at)` 后 join 回 `agent_versions v`，并 `SELECT v.*`。
  - 通过 `<foreach>` 传入 agentIds。

2) 重写工作区版本选取逻辑（不再查全量版本再 groupBy）：
- 在 [AgentWorkspaceServiceImpl.getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L63-L145)：
  - 先取 workspace agentIds（保序）。
  - 调 mapper 查 latest PUBLISHED map。
  - 计算缺失 agentIds，再查 latest REMOVED map。
  - 合并为 `bestVersionMap`。
  - 组装 DTO：有版本用版本；无版本回退用 agents（保持你前面要的“未发布也可见”）。

3) 稳定性细节
- 处理 published_at 相同导致 join 多行的边缘：在 join 条件中再加 `MAX(id)`（如果 id 单调）或在 Java 里对同一 agentId 再做一次比较只保留 1 条。
- agent_workspace 残留但 agents/versions 都不存在则跳过（保持现状健壮性）。

## 验证
- 编译验证：mvn -DskipTests compile。
- 逻辑验证：
  - agent 有很多版本时，SQL 返回行数应接近 agent 数量，而不是版本总数。
  - 未发布无版本的 agent 仍能通过 agents 回退展示。

确认后我就开始落地改造（新增 mapper SQL + 重写 getAgents 逻辑 + 编译验证）。