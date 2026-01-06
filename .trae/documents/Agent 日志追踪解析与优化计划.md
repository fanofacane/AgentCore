## 设计要点

* 观察者模式：追踪采集与持久化通过事件发布/监听解耦，核心类 [TraceCollector](file:///d:/software/GitProject/AgentCore/src/main/java/com/sky/AgentCore/service/trace/TraceCollector.java) 发布事件，监听器 [TraceEventListener](file:///d:/software/GitProject/AgentCore/src/main/java/com/sky/AgentCore/service/trace/TraceEventListener.java) 处理并调用领域服务落库。

* 上下文贯通：使用 InheritableThreadLocal 携带 TraceContext，保障子线程（如 TokenStream 回调）继承。实现见 [TracingMessageHandler](file:///d:/software/GitProject/AgentCore/src/main/java/com/sky/AgentCore/service/chat/handler/TracingMessageHandler.java#L56-L66、#L254-L269)。

## 优点说明

* 强解耦：业务主流程与追踪持久化完全分离，便于扩展与维护。

* 低侵入：关键节点仅发布事件、读写 ThreadLocal，最小化改动面。

* 扩展友好：可平滑新增追踪维度（如降级、异常详情）与新处理器而不影响主流程。

* 子线程安全贯通：在回调链中设置/清理 ThreadLocal，保证上下文可用且不泄漏。

* 可观测与容错：监听器内集中落库与统计，失败不阻塞主流程并留可重试空间。

## 注意事项

* 线程池风险：InheritableThreadLocal 不适用于线程池复用场景，需替换为 TransmittableThreadLocal 并包装线程池（已在代码注释中提醒）。

* 清理责任：在完成/异常回调中显式 remove，避免上下文污染与内存泄漏。

## 面试题与答案

1. 为什么用观察者模式做链路追踪？

   * 解耦、可扩展、异步处理；失败不影响主流程；便于按需订阅不同追踪事件。

2. InheritableThreadLocal 与 ThreadLocal 的差异？在项目中如何使用？

   * InheritableThreadLocal 支持子线程继承；本项目在 TokenStream 回调包装中设置/清理确保贯通；线程池需 TTL 替代。

3. 如何避免上下文污染与内存泄漏？

   * 在每次回调开始 set，finally 块 remove；线程池场景用 TTL；在请求结束统一清理。

4. 事件驱动下的一致性如何保证？

   * 领域服务集中落库，步骤明细与汇总更新分离；完成事件统一收敛状态；可加入重试与幂等。

5. 与 MDC/MQ Header 的协同方式？

   * TraceId 为 sessionId；MQ 发布写入 Header（seqId），消费者读入 MDC；日志与事件两条线共同保证跨组件追踪。

