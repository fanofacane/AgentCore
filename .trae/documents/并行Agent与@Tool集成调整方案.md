## 总览

* 当前工程未使用 AiServices.builder；并行工具调用由 ParallelStreamingAgent 自行实现。

* 若保留现有架构：继续由 ParallelStreamingAgent 并发执行工具；@Tool 可通过 Tools.from 适配为 ToolSpecification/ToolExecutor 后并入现有工具集。

* 若迁移到 AiServices：让框架托管工具执行与并发，删除自研并行逻辑。

## 保留现有架构的改进

1. 在 AbstractMessageHandler.buildStreamingAgent 中，将 wrapTools(agent) 返回的 Map 与 Tools.from(new XxxTools()) 生成的 Map 合并。
2. XxxTools 类中为需要暴露的业务方法加上 @Tool 注解并提供描述与参数定义；Tools.from 会自动生成 ToolSpecification 与 ToolExecutor。
3. 保留 ParallelStreamingAgent 的并行执行逻辑（CompletableFuture.allOf），可增加配置开关 enableConcurrentToolExecution 以便运行时切换并发策略。
4. 验证：构造含多 @Tool 的请求，确认并发执行、回调与内存写入正常；记录性能指标与错误处理。

## 迁移到 AiServices 的方案（可选）

1. 新建服务：AiServices.builder(Agent.class).streamingChatModel(model).chatMemory(memory).tools(new XxxTools()).build()。
2. 使用 executeToolsConcurrently 或框架默认并发策略；移除 ParallelStreamingAgent 中的手工并发与工具调度代码。
3. 将 ToolProvider 与 MCP 集成迁移为 AiServices 的工具提供方式或在调用前合并为工具集。
4. 验证：流式响应、工具调用、记忆写入与资源清理均由框架托管后仍满足业务需求。

## 推荐路径

* 近期：先按“保留现有架构的改进”接入 @Tool，风险低、改动小。

* 中长期：视需求逐步迁移到 AiServices，降低自维护成本并利用框架最新能力。

## 交付项

* XxxTools 示例类与接入代码（Tools.from 合并）。

* enableConcurrentToolExecution 配置开关。

* 验证用例与性能对比报告。

