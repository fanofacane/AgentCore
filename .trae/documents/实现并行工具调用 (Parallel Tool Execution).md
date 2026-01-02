# 并行工具调用实现计划

您观察到的模型多次调用工具导致响应缓慢，确实是因为当前的工具执行机制是串行的。为了解决这个问题，我们需要自定义 Agent 的执行逻辑，手动接管“模型 -> 工具 -> 模型”的循环，并在接收到多个工具调用请求时并发执行它们。

## 核心变更

我们将创建一个新的 Agent 实现类 `ParallelStreamingAgent`，替代目前由 `AiServices` 自动生成的代理。这个类将负责管理流式对话，并实现并行的工具执行逻辑。

### 1. 新增 `ParallelStreamingAgent` 类

位置：`com.sky.AgentCore.service.agent.Impl.ParallelStreamingAgent`

该类将实现 `Agent` 接口，包含以下核心逻辑：

* **手动管理对话循环**：不再依赖框架自动处理，而是手动调用 `StreamingChatModel`。

* **并行执行工具**：当模型返回多个 `ToolExecutionRequest` 时，使用 `CompletableFuture` 并发调用对应的 `ToolExecutor`。

* **状态管理**：维护对话历史（Memory），确保在工具执行前后正确更新上下文。

### 2. 修改 `AbstractMessageHandler`

位置：`com.sky.AgentCore.service.chat.handler.AbstractMessageHandler`

我们需要修改 `buildStreamingAgent` 方法，使其返回我们要实现的 `ParallelStreamingAgent` 实例，而不是使用 `AiServices` 构建的默认代理。

## 详细步骤

1. **创建** **`ParallelStreamingAgent.java`**

   * 实现 `Agent` 接口的 `chat(String userMessage)` 方法。

   * 实现一个内部的 `TokenStream`，用于向上传递流式响应和事件。

   * 在 `StreamingResponseHandler` 的 `onComplete` 回调中，检测 `hasToolExecutionRequests()`。

   * 如果存在工具调用，遍历请求列表，使用 `CompletableFuture.supplyAsync` 并行执行所有工具。

   * 等待所有工具执行完毕，将结果汇总添加到 Memory，然后递归调用模型进行下一轮生成。

2. **集成到** **`AbstractMessageHandler`**

   * 在 `buildStreamingAgent` 方法中，获取 `builtInTools`（Map\<ToolSpecification, ToolExecutor>）。

   * 实例化 `ParallelStreamingAgent`，传入 `StreamingChatModel`、`MessageWindowChatMemory` 和工具映射表。

3. **验证**

   * 确保并行执行时，所有工具的结果都能正确回传给模型。

   * 确保计费、日志和回调钩子（如 `onToolExecuted`）依然正常工作。

通过这个方案，我们可以显著提高多工具调用场景下的响应速度，因为所有工具将同时开始执行，总耗时将取决于最慢的那个工具，而不是所有工具耗时的总和。
