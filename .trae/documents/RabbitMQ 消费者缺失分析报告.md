# 问题分析：消息堆积原因

经过对代码库的深入分析，您的判断是正确的，**确实是消费者出现了问题**，具体表现为：**当前 `AgentCore` 服务中根本没有该队列的消费者代码**。

以下是详细的排查证据：

1.  **生产者存在且工作正常**：
    *   代码位置：`RagQaDatasetAppService.java`
    *   行为：文件上传后，系统正确调用了 `messagePublisher.publish` 发送消息到 `rag.doc.task.syncOcr.queue-10`。
    *   日志印证：日志显示 "Auto-preprocessing started"，说明消息已成功发出。

2.  **消费者缺失**：
    *   在整个项目中搜索了 `rag.doc.task.syncOcr.queue-10` 相关的监听器。
    *   **结果**：找不到任何 `@RabbitListener` 监听此队列。
    *   **对比**：我找到了后续步骤的消费者 `RagDocStorageConsumer`（监听 `rag.doc.task.syncStorage.queue10`），这说明这是一个多阶段的处理流程：`上传 -> OCR处理(缺失) -> 向量化存储(存在)`。

3.  **架构推断**：
    *   通常 OCR（光学字符识别）任务比较重，或者依赖特定的 Python 库（如 PaddleOCR），因此设计上很可能是一个**独立的外部服务**（可能是 Python 服务）来消费这个队列。
    *   当前不仅是“处理慢”，而是“无人处理”，导致消息在 RabbitMQ 中状态为 `Ready: 1`, `Unacked: 0`, `deliver: 0.00/s`。

## 解决方案建议

请根据您的实际情况选择一种方案：

### 方案 A：启动外部 OCR 服务（推荐）
如果您有一个配套的 OCR 服务（例如 `AgentCore-OCR` 或 Python 脚本），请确保它**已启动**并且配置了正确的 RabbitMQ 连接信息。一旦启动，它会自动消费堆积的消息。

### 方案 B：在此项目中实现 OCR 消费者
如果您的意图是由 `AgentCore` 直接处理 OCR（例如调用您日志中出现的 `gpt-5` 模型接口进行多模态识别，或者使用 Java OCR 库），那么我们需要**新建一个消费者**。

如果您选择 **方案 B**，我可以为您创建一个新的消费者类 `RagDocOcrConsumer`，逻辑如下：
1.  监听 `rag.doc.task.syncOcr.queue-10`。
2.  接收文件信息。
3.  调用 AI 模型（您日志中配置的 `https://yunwu.ai/v1`）或 OCR 工具提取文本。
4.  将提取结果发送到下一个队列 `rag.doc.task.syncStorage.queue-10`，打通流程。

**您希望我为您实现方案 B 吗？**