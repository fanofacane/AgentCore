## 现状分析（为什么可能慢）
- 当前 [AgentWorkspaceServiceImpl.getAgents](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/AgentWorkspaceServiceImpl.java#L63-L145) 至少会做 3 次查询：
  1) `agent_workspace` 查 userId 的 agentIds
  2) `agents` 查这些 agentIds 的全部列（未做 select 列裁剪）
  3) `agent_versions` 查这些 agentIds 且 `publish_status in (PUBLISHED, REMOVED)` 的**所有版本**，再在 Java 里 groupBy + max
- 真正的性能风险主要在第 3 步：如果每个 agent 有很多版本，你会把“全量版本行”拉到内存里再筛选，IO/内存/GC 都会变重；另外循环里 `BeanUtils.copyProperties` 反射拷贝也会放大 CPU 开销。

## 优化方向（从收益最高到最低）
1) **把“选每个 agent 的最佳版本”下推到数据库**
- 目标：版本表最多返回“每个 agent 1 条版本”（或两条：published 优先，没 published 再取 removed）。
- 实现方式（兼容性强、容易落地）：
  - 先查“每个 agent 最新 PUBLISHED 版本”（只返回 1 条/agent）
  - 对没有 published 的 agentIds 再查“每个 agent 最新 REMOVED 版本”
  - Java 只做 map 合并，不再 groupBy 全量版本。

2) **列裁剪（select 需要的字段）**
- `agents`/`agent_versions` 如果有大字段（systemPrompt、toolPresetParams 等）且工作区列表不需要，建议只 select 列表页所需字段，显著减小网络与序列化成本。

3) **替换 BeanUtils 反射拷贝**
- 用手写赋值或 MapStruct/Assembler（项目里已有 Assembler 模式）可减少 CPU。

4) **分页/限制工作区数量**
- 若工作区可能上百上千个 agent，建议接口支持分页（page/size）或默认只返回前 N 个。

5) **索引与缓存（可选）**
- 索引：`agent_workspace(user_id)`、`agent_versions(agent_id, publish_status, published_at)`、（若有 deleted_at）部分索引/联合索引。
- 缓存：按 userId 缓存工作区列表短 TTL（例如 30s），在 add/delete/publish 时主动失效。

## 我将如何改代码（确认后执行）
1. 在 `AgentVersionMapper` 新增 2 个查询方法：
   - `selectLatestByAgentIdsAndStatus(agentIds, status)`：每个 agent 只取最新一条（通过子查询 group by + max(published_at) join）。
2. 重写 `getAgents`：
   - 获取 workspace agentIds（保序）
   - 一次性查 agents（只 select 列表需要字段）形成 map
   - 一次性查 latest published 版本形成 map；对缺失的 agentIds 再查 latest removed 版本形成 map
   - 组装 DTO（避免 BeanUtils 或减少拷贝字段）
3. 加一个简单的性能日志（仅记录耗时，不打敏感数据），用于对比优化前后。
4. 编译验证 + 用模拟数据（大量版本）做简单压测对比。

如果你点确认，我就按以上步骤把优化落地到代码里。