## 现状结论（基于只读代码审查）
- `PortalAgentController.deleteAgent` 直接调用 `AgentAppServiceImpl.deleteAgent(agentId, userId)`，该方法会删除/逻辑删除：agents、agent_versions、agent_workspace、agent_widgets、sessions，以及会话下的 agent_execution_summary / agent_execution_details，并清理该 agent 的 scheduled_tasks。
- 该实现“确实涉及很多表”，但目前存在明显风险：
  - **残留数据**：未清理 `messages`（会话消息）与 `context` 等会话周边表；删除 Agent 后会留下大量“已删会话的消息/上下文”。
  - **误删/影响面扩大**：对 `agent_versions/agent_workspace/agent_widgets` 的删除条件仅按 `agent_id`，不带 `userId`；如果产品语义是“删除=全网下架”，这可接受；若是“仅删除自己”，会误删其他用户的安装/组件。
  - **删除失败风险**：多处使用 `checkedDelete`（affected==0 直接抛异常）。当某个 Agent 没有 widget、没有会话、没有 trace 明细时，会导致删除流程直接失败回滚。

## 修复目标
- 删除 Agent 时：
  - 不因“无关联数据”而失败。
  - 级联清理会话相关数据（至少 messages/context），避免残留。
  - 明确删除语义（owner 删除是否应影响 workspace/versions/widgets 的全局数据）。
  - 保障一致性：所有删除在一个事务中完成，必要时分批删除避免 in-list 过长。

## 具体改造步骤（待你确认后执行）
1. **把 deleteAgent 的级联删除改为“可空删除”**：对 widget/trace/session 删除改用 `delete(...)` 或 `in` 前先判空；不再用 `checkedDelete` 强制 affected>0。
2. **复用单会话删除逻辑**：遍历该 Agent 的 sessionIds，调用现有 `AgentSessionServiceImpl.deleteSession(sessionId, userId)`（它会删除 messages），或抽一个 `deleteSessionsCascade(sessionIds, userId)` 统一处理，避免逻辑分散。
3. **补齐会话周边表清理**：在删除会话时同步删除/逻辑删除 `context` 表（以及任何其他以 `session_id` 关联的表），保证无残留。
4. **明确“全局/用户级”删除边界**：
   - 如果语义是“owner 删除=全网下架”：保留按 `agent_id` 删除 workspace/versions/widgets。
   - 如果语义是“仅删除自己”：workspace/widgets 删除必须加 `user_id`；versions 可能不应删除（或仅删除自己创建的版本）。
   我会按代码中现有权限模型给出默认更安全的实现（优先不跨用户删除），并保留可配置开关。
5. **安全与性能**：对 sessionIds 很多的情况做分批（chunk）删除，避免 SQL `IN (...)` 过长；并补充必要的索引建议（如 `messages(session_id)`）。
6. **验证**：补一组最小集成测试/或本地脚本验证：
   - 没有 widget / 没有 session 的 Agent 也能成功删除；
   - 删除后 sessions/messages/context/trace/scheduled_tasks 均不可再查询到（符合逻辑删除规则）。

## 交付物
- 修改 `AgentAppServiceImpl.deleteAgent` 及相关 service/mapper；必要时新增用于级联删除的 DomainService。
- 若需要，补充数据库索引/约束建议文档。
