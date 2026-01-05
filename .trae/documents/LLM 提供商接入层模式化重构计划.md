## 目标

* 抽象统一的模型接入层，支持多提供商按协议扩展、按配置选择与管理。

* 去除静态工厂与条件分支膨胀，提升可测试性与扩展性。

## 设计模式

* 工厂方法 + 抽象工厂：按 ProviderProtocol 创建 ChatModel/StreamingChatModel。

* 策略 + 注册表：以协议为键注册具体 Provider 策略，实现开闭原则。

* 适配器：将各 SDK 适配为统一接口 ChatModel/StreamingChatModel（沿用 LangChain4j）。

* 门面：保留 LLMServiceFactory 作为对外统一入口。

## 类与接口

* 新增 Provider 接口：

  * 方法：createChatModel(ProviderConfig, LLMModelConfig)、createStreamingChatModel(...)

  * 方法：getProtocol(): ProviderProtocol

* 具体实现：OpenAIProvider、AnthropicProvider、GeminiProvider、QwenProvider、ByteDanceProvider...

* 统一配置：沿用 ProviderConfig、LLMModelConfig；移除其上的 @Service 注解，作为纯 POJO。

## 注册表与工厂

* ProviderRegistry（@Component）：构造注入 List<Provider>，建立 Map\<ProviderProtocol, Provider>。

* LLMServiceFactory（@Component）：

  * 注入 ProviderRegistry。

  * 依据 ProviderEntity/ModelEntity/LLMModelConfig 组装 ProviderConfig。

  * 通过 registry.get(protocol) 创建 ChatModel/StreamingChatModel。

* 删除/保留兼容：将 LLMProviderFactory 标记为 Deprecated，并由注册表替代其逻辑。

## 配置与管理

* ProviderEntity/ModelEntity 保持不变，增强校验：协议存在性校验、必填项校验、BaseURL/Key 合法性。

* Converter 保持：ProviderConfigConverter、ProviderProtocolConverter、LLMModelConfigConverter。

## 接入流程

* 控制器/应用服务不变；消息处理器继续通过 LLMServiceFactory 获取模型。

* ConversationAppServiceImpl 构造默认 LLMModelConfig 的逻辑保留。

## 测试与验证

* 单元测试：ProviderRegistry 注册与查找；各 Provider 的模型创建；LLMServiceFactory 配置组装与错误路径。

* 集成测试：多协议选择、同步与流式调用；异常降级（未注册协议 -> 业务异常或安全降级）。

## 兼容与迁移

* 逐步替换静态 LLMProviderFactory 的引用为注册表调用；保留兼容层一版迭代后移除。

* 保持对 ChatModel/StreamingChatModel 的向后兼容，避免调用方改动。

## 风险与回滚

* 风险：注册失败或 Bean 加载问题影响模型创建。

* 回滚：保留原静态工厂作为兜底，出现问题可快速切换回旧路径。

