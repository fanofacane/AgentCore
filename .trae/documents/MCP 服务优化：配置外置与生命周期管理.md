# MCP 服务优化计划：生命周期管理与配置外置

您提到的两个问题非常关键：
1.  **心跳/连接未断开**：当前的 `McpClient` 和 `McpTransport` 是在每次请求时创建的，但使用完后没有显式关闭（Close），导致连接一直保持，后台持续发送心跳或 Ping 消息。
2.  **配置硬编码**：MCP 服务的 URL 写死在代码中，不便于管理和切换环境。

## 解决方案

### 1. 配置外置化 (YAML Configuration)

我们将创建一个新的配置类 `McpConfig`，用于映射 `application.yml` 中的 MCP 配置。

**步骤：**
1.  **定义配置属性类**：创建 `McpProperties` 类，使用 `@ConfigurationProperties(prefix = "agent.mcp")`。
    *   结构示例：`agent.mcp.servers` 是一个列表，每个元素包含 `name` 和 `url`。
2.  **更新 YAML**：在 `application.yml` 中添加具体的 MCP 服务地址。

### 2. 生命周期管理 (Lifecycle Management)

我们需要确保每次对话结束后，创建的 MCP 客户端资源能被正确释放。

**步骤：**
1.  **修改 `AbstractMessageHandler`**：
    *   引入 `McpProperties` 依赖。
    *   在 `buildStreamingAgent` 方法中，根据配置动态创建 `McpClient` 列表。
    *   **关键点**：将创建的 `McpClient` 实例保存起来（例如放入一个列表）。
    *   在 `ParallelStreamingAgent` 执行完毕或发生异常时，遍历这个列表调用 `close()` 方法。
2.  **优化 `ParallelStreamingAgent`**：
    *   由于 `Agent` 接口本身没有 `close` 方法，我们需要在 `TokenStream` 的 `onComplete` 或 `onError` 回调中，或者在外部控制层，显式地关闭这些资源。
    *   **更好的方案**：让 `ParallelStreamingAgent` 实现 `AutoCloseable` 接口（或者提供一个 `close()` 方法），并在 `AbstractMessageHandler` 的 `processChat` 流程结束后（`finally` 块中）调用它。
    *   由于 `ParallelStreamingAgent` 持有 `ToolProvider`，而 `ToolProvider` 持有 `McpClient`，我们可以在 `ParallelStreamingAgent.close()` 中关闭所有 `ToolProvider`（如果它们支持关闭）或者直接在 `AbstractMessageHandler` 中管理 `McpClient` 的生命周期。

**推荐方案：在 `AbstractMessageHandler` 中使用 `try-with-resources` 或 `finally` 块管理 `McpClient`。**

由于 `Agent` 的执行是异步流式的，我们不能简单的在 `buildStreamingAgent` 返回后就关闭。我们需要在流式响应结束（`onCompleteResponse` / `onError`）时触发关闭操作。

**修正方案：**
1.  修改 `ParallelStreamingAgent`，让它持有一个 `Runnable closeAction`。
2.  在 `AbstractMessageHandler` 创建 `McpClient` 时，定义这个 `closeAction`（即遍历关闭所有 client）。
3.  `ParallelStreamingAgent` 在检测到对话结束（无论是正常结束还是异常）时，执行这个 `closeAction`。

## 详细实施步骤

1.  **创建 `McpProperties.java`**
    *   路径：`com.sky.AgentCore.dto.config.McpProperties`
    *   内容：包含 `List<McpServerConfig> servers`。

2.  **配置 `application.yml`**
    *   添加 `agent.mcp.servers` 配置项。

3.  **修改 `AbstractMessageHandler.java`**
    *   注入 `McpProperties`。
    *   在 `buildStreamingAgent` 中遍历配置创建 `McpClient`。
    *   定义关闭逻辑：`List<McpClient> clientsToClose`。
    *   将关闭逻辑传递给 `ParallelStreamingAgent`。

4.  **修改 `ParallelStreamingAgent.java`**
    *   构造函数增加 `Runnable onClose` 参数。
    *   在 `TokenStream` 的 `onCompleteResponse` 和 `onError` 回调的最后，执行 `onClose.run()`。

这样既解决了配置灵活性的问题，又确保了连接能被及时关闭，不再占用资源。
