# AgentNexus 架构与动态配置说明

## 核心概念
- **AgentDefinition**：代表业务级 Agent，持久化编码、名称、启用状态与当前激活版本。
- **AgentVersion**：Agent 的发布快照，记录版本号、入口节点、元数据与状态（`DRAFT/ACTIVE/ARCHIVED`）。
- **AgentNode**：责任链中的单个节点，保存节点类型（`PROMPT_TEMPLATE`、`MODEL_INVOKE`、`TOOL_INVOKE`）、顺序、指向的下一个节点以及 JSON 配置。所有节点均可热更新并由 API 写入数据库。

## 运行时责任链
1. 配置 API 写入/更新 Agent、Version、Node 定义，数据由 JPA 自动同步至数据库（默认 H2，生产指向 MySQL）。
2. `AgentRuntimeLoader` 依据 AgentCode + Version 构建运行时快照，内含入口节点与节点映射，并带有本地缓存（发布或节点更新时自动失效）。
3. `AgentChainExecutor` 读取快照，按 `nextNodeKey` 串联节点，并将上下文（输入/记忆/日志）传递给各节点处理器。
4. NodeProcessor 由 Spring 发现并注册，当前内置 Prompt 渲染、DashScope 模型调用（可降级为本地 mock）以及工具模拟，后续可以新增 RAG、Webhook 等节点类型，只需实现 `AgentNodeProcessor` 接口并声明 Bean 即可即时生效。

## 配置示例
```json
POST /agents/{code}/versions/{version}/nodes
{
  "nodeKey": "prompt-node",
  "nodeType": "PROMPT_TEMPLATE",
  "sequence": 1,
  "nextNodeKey": "model-node",
  "entry": true,
  "config": {
    "template": "你好，{{name}}，请推荐航班",
    "outputKey": "prompt"
  }
}
```
随后追加 `MODEL_INVOKE` 节点，把 `prompt` 作为输入并将结果写入 `answer`，最后通过 `POST /agents/{code}/versions/{version}/deploy` 激活版本。运行时调用 `POST /agents/execute`，即可基于最新快照执行责任链而无需重启服务。

## MySQL 部署提示
- 在 `application.yml` 或独立 profile 中配置 `spring.datasource.*` 指向 MySQL；`ddl-auto=update` 可帮助初始化表结构，生产建议改为迁移脚本。
- 如需连接池或多数据源，可将 DataSource 抽象为 `@ConfigurationProperties(prefix="spring.datasource")` 并引入 HikariCP。

## 扩展建议
- 为 Tool/Model 节点补齐鉴权信息，可在节点 JSON 中保存 `toolName`、`timeout`、`retries` 等字段，由处理器解释。
- 对高频 Agent 可在 `AgentRuntimeLoader` 上叠加二级缓存（如 Redis）以减少数据库命中。
- 接入 WebConsole 时，可利用 `GET /agents/{code}/versions`、`GET /agents/{code}/versions/{version}/nodes` 构建可视化 DAG，并通过前端拖拽生成 Node JSON。
