# 修复 RabbitMQ Double Ack 问题

## 问题分析

`ShutdownSignalException: unknown delivery tag 1` 异常的核心原因是 **Double Ack（重复确认）**。

1. **现象**：

   * 你的代码 `RagDocStorageConsumer` 中手动执行了 `channel.basicAck(deliveryTag, false)`。

   * 但是，Spring Boot 的 RabbitMQ 容器默认配置（或当前生效配置）是 `acknowledge-mode: AUTO`（自动确认）。

   * **结果**：你手动 Ack 了一次，方法返回后，Spring 框架又尝试自动 Ack 一次。第二次 Ack 时，消息已经被确认并移除，RabbitMQ 认为该 delivery tag 无效，因此关闭 Channel 并抛出 `ShutdownSignalException`。

2. **连锁反应**：

   * Channel 关闭导致 Consumer 重启。

   * 重启可能导致消息重新投递或逻辑重复执行。

   * 重复执行导致状态机接收到重复的 `COMPLETE_EMBEDDING_PROCESSING` 事件，从而报错“当前状态\[处理完成]不支持事件...”。

3. **配置错位**：

   * `application.yml` 中配置了 `acknowledge-mode: manual`，但这并没有生效。

   * 原因是 `RabbitMessageConfig.java` 中手动创建 `SimpleRabbitListenerContainerFactory` 时，**没有使用** **`SimpleRabbitListenerContainerFactoryConfigurer`** **来应用配置文件中的属性**。它直接 `new` 了一个工厂，导致使用默认值（AUTO），忽略了 YAML 中的配置。

## 修复方案

### 1. 修正 `RabbitMessageConfig.java`

我们将修改 `rabbitListenerContainerFactory` 方法，注入 `SimpleRabbitListenerContainerFactoryConfigurer`，并调用 `configurer.configure(...)`。这将确保 `application.yml` 中的 `acknowledge-mode: manual` 配置被正确应用到容器工厂中。

**修改前**：

```java
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
}
```

**修改后**：

```java
public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory connectionFactory, 
        SimpleRabbitListenerContainerFactoryConfigurer configurer) { // 注入 Configurer
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory); // 应用 yml 配置 (包括 manual ack)
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
}
```

### 2. 验证效果

* 容器将切换为 `MANUAL` 模式。

* 你的 `RagDocStorageConsumer` 中的 `channel.basicAck` 将是唯一的一次确认。

* 这将消除 Double Ack，解决 `ShutdownSignalException`。

* 消除异常重启后，状态机的重复触发问题也应随之解决。

## 执行步骤

1. 修改 `src/main/java/com/sky/AgentCore/config/mq/RabbitMessageConfig.java`。

