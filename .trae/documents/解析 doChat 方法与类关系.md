# MCP (ToolProvider) 支持恢复计划

您指出的问题非常关键。在切换到自定义的 `ParallelStreamingAgent` 后，我们确实遗漏了对外部 `ToolProvider`（即 MCP 服务）的支持。原有的 `AiServices` 自动处理了这部分逻辑，现在我们需要手动将其加回来。

## 核心挑战

1.  **动态获取工具定义**：`ToolProvider` 是一个动态接口，它可以提供一组工具定义（`ToolSpecification`）和对应的执行逻辑（`ToolExecutor`）。
2.  **统一管理**：我们需要将“内置工具”和“外部 MCP 工具”合并管理，统一在 `doChat` 中发给大模型，并统一进行并行调度。

## 解决方案

### 1. 修改 `ParallelStreamingAgent`

我们需要改造 `ParallelStreamingAgent` 的构造函数和内部逻辑，使其能够接收并处理 `ToolProvider`。

**变更点：**
*   **增加字段**：添加 `ToolProvider toolProvider` 字段。
*   **构造函数更新**：接收 `ToolProvider` 参数。
*   **合并工具定义**：
    *   在初始化时（或每次调用 `chat` 时，取决于 MCP 是否动态变化），调用 `toolProvider.provideTools(request)` 获取外部工具的 `ToolSpecification` map。
    *   将这些外部工具与内置工具合并到一个总的 `toolSpecifications` 列表和 `toolExecutorMap` 中。

### 2. 更新 `AbstractMessageHandler`

在 `buildStreamingAgent` 方法中，将 `toolProvider` 传递给新的 `ParallelStreamingAgent`。

## 详细实施步骤

1.  **修改** **`ParallelStreamingAgent.java`**
    *   添加 `ToolProvider` 成员变量。
    *   修改构造函数签名：`public ParallelStreamingAgent(..., ToolProvider toolProvider)`。
    *   在 `chat()` 方法中：
        *   调用 `toolProvider.provideTools(toolProviderRequest)` 获取外部工具。
        *   将外部工具的 `ToolSpecification` 和 `ToolExecutor` 合并到现有的集合中。
        *   确保合并后的工具列表传递给 `ChatRequest`。

2.  **修改** **`AbstractMessageHandler.java`**
    *   在创建 `ParallelStreamingAgent` 实例时，传入 `toolProvider` 参数。

3.  **验证**
    *   确保内置工具和 MCP 工具都能被正确识别。
    *   确保两者都能在并行执行框架下正常工作。

通过这个修改，我们将完美恢复对 MCP 服务的支持，同时保留并行调用的高性能特性。
