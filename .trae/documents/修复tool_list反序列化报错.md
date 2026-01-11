## 实施目标

* 保证每个工具一旦完成，立即向前端发送两类消息：

  * TOOL\_CALL（非结束型）：提示某工具已执行

  * TOOL\_RESULT（或多段 TOOL\_RESULT\_CHUNK）：下发该工具的结果内容，支持长文本分片

* 不等待所有工具完成才下发；等待全部工具完成仅用于递归 doChat 进入下一轮模型响应

## 关键依据

* 并行工具执行已在每个工具完成时调用 onToolExecutedHandler（发生在 join 之前）：[ParallelStreamingAgent.java:L212-L223](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/agent/Impl/ParallelStreamingAgent.java#L212-L223)

* 只需在 onToolExecuted 回调里即时发送结果，即可实现“每个工具完成即下发”，无须改动并行执行策略

## 代码级改动

* 变更位置：[AbstractMessageHandler.java:onToolExecuted](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/chat/handler/AbstractMessageHandler.java#L369-L392)

* 修改点：

  1. 将现有工具调用通知从结束型改为普通消息：

     * 替换 transport.sendMessage(connection, AgentChatResponse.buildEndMessage(message, MessageType.TOOL\_CALL))

     * 为 transport.sendMessage(connection, AgentChatResponse.build(message, MessageType.TOOL\_CALL))
  2. 立即发送工具结果：

     * 若 MessageType 缺少 TOOL\_RESULT/TOOL\_RESULT\_CHUNK，先在枚举中扩展

     * 在 onToolExecuted 中读取 toolExecution.result()

     * 对结果进行分片发送（可选，提升“流式感”）：

```java
// 工具结果分片发送示例（带注释）：
private void sendToolResultStreaming(Object connection, MessageTransport<Object> transport, String toolName, String result) {
    // 常量：每片大小（可根据前端体验调整）
    final int CHUNK_SIZE = 2048;
    // 工具结果为空时，直接发送空结果提示
    if (result == null || result.isEmpty()) {
        transport.sendMessage(connection, AgentChatResponse.build("" , MessageType.TOOL_RESULT));
        return;
    }
    // 分片循环发送 CHUNK 消息
    int len = result.length();
    for (int i = 0; i < len; i += CHUNK_SIZE) {
        int end = Math.min(i + CHUNK_SIZE, len);
        String chunk = result.substring(i, end);
        // 每片作为非结束型消息下发，前端按序拼接显示
        transport.sendMessage(connection, AgentChatResponse.build(chunk, MessageType.TOOL_RESULT_CHUNK));
    }
    // 发送最终的 TOOL_RESULT，作为这些分片的“完成标识”
    transport.sendMessage(connection, AgentChatResponse.build("[工具结果完成]" , MessageType.TOOL_RESULT));
}
```

1. 在 onToolExecuted 内调用上述逻辑：

```java
// onToolExecuted 回调内（重点片段）：
tokenStream.onToolExecuted(toolExecution -> {
    String callMsg = "执行工具:" + toolExecution.request().name();
    // A. 非结束型工具调用通知
    transport.sendMessage(connection, AgentChatResponse.build(callMsg, MessageType.TOOL_CALL));

    // B. 发送工具结果（分片或一次性）
    String toolResult = toolExecution.result();
    // 分片版：
    sendToolResultStreaming(connection, transport, toolExecution.request().name(), toolResult);
    // 一次性版（若结果较短可直接）：
    // transport.sendMessage(connection, AgentChatResponse.build(toolResult, MessageType.TOOL_RESULT));

    // C. 持久化工具结果（建议将 CHUNK 在服务端聚合后持久化最终结果）
    MessageEntity toolResultMessage = createLlmMessage(chatContext);
    toolResultMessage.setMessageType(MessageType.TOOL_RESULT);
    toolResultMessage.setContent(toolResult);
    messageDomainService.saveMessageAndUpdateContext(Collections.singletonList(toolResultMessage), chatContext.getContextEntity());

    // D. 工具调用完成钩子（包含结果供审计/埋点）
    ToolCallInfo toolCallInfo = buildToolCallInfo(toolExecution);
    onToolCallCompleted(chatContext, toolCallInfo);
});
```

* 前端：

  * SSE 订阅处理新增的 MessageType.TOOL\_RESULT 与 TOOL\_RESULT\_CHUNK

  * 对 CHUNK 拼接展示；收到 TOOL\_RESULT 作为完成标识

  * TOOL\_CALL 不再使用“结束型消息”，只作为提示气泡/进度条更新

## 验证与效果

* 多工具并行时，每个工具完成即前端看到 TOOL\_CALL 提示与对应 TOOL\_RESULT（或 CHUNK 流）

* 不再出现“等全部工具结束后集中一次性返回”的体验

* 模型在 allOf.join 后递归 doChat，继续生成最终回答，前端体验更平滑

## 注意事项

* 大结果的分片大小需根据前端渲染与网络情况调优

* 结果内容可能包含敏感信息，需按现有策略过滤与审计

* 计费与 token 统计逻辑不变，工具结果仅作为辅助输出，不参与模型 token 计费

