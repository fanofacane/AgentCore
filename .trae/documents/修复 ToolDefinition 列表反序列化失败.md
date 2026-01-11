## 现象与根因

* 报错来自 [JsonUtils.parseArray](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/utils/JsonUtils.java#L72-L90)：代码把某段 JSON 当作 `List<ToolDefinition>` 解析。

### 但异常信息明确指出是从 **String 值** 反序列化：`deserialize from String value ('a1b2...') (through reference chain: ArrayList[0])`，说明输入 JSON 形态更像 `[ "a1b2c3...", "..." ]`（字符串数组），而不是 `[{"name":...}]`（对象数组）。

* 同时，当前 [ToolDefinition](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/dto/tool/ToolDefinition.java) 是不可变 + Builder 模式：字段 `final`、唯一构造器 `private ToolDefinition(Builder)`，没有 `@JsonCreator/@JsonProperty`，即便输入是对象数组，Jackson 也很难直接构造实例。
* 触发点在 MyBatis TypeHandler：[ToolDefinitionListConverter](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/converter/ToolDefinitionListConverter.java#L11-L28) 绑定到 [ToolVersionEntity.toolList](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/dto/tool/ToolVersionEntity.java#L60-L65)。

## 修复目标

* 让 `tool_versions.tool_list` 的真实存储结构与 Java 字段类型一致。

* 同时提升容错/兼容能力，避免存量数据或未来变更导致再次解析失败。

## 实施方案（默认走“兼容优先”）

### 方案 1（推荐默认）：兼容两种 JSON 形态

* 在 `ToolDefinitionListConverter.parseJson` 中先解析为 `JsonNode`：

  * 若数组元素是 `textual`（字符串数组），则按 `List<String>` 解析并：

    * A) 若业务语义就是“工具ID列表”，则把实体字段改为 `List<String>` 并更名（例如 `toolIdList`），对应创建新的 `StringListConverter`；

    * B) 若业务语义必须是“工具定义列表”，则需要在此处根据 ID 去查工具定义再组装（需要确认项目里是否存在按ID查询工具定义的仓储/服务；当前代码库未发现写库/查询点）。

  * 若数组元素是 `object`（对象数组），则按 `List<ToolDefinition>` 解析。

* 同时增强 [JsonUtils.parseArray](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/utils/JsonUtils.java#L72-L90) 的错误日志：打印目标类型 + 截断后的 JSON 摘要，便于排障。

### 方案 2：统一存储为对象数组

* 为 `ToolDefinition` 与内部 `ParameterDefinition` 增加 `@JsonCreator` 构造器 + `@JsonProperty`，让对象数组可以直接反序列化。

* 需要同步把数据库里已存在的 `tool_list` 从 `['uuid', ...]` 迁移为 `[{...}, ...]`（或新增字段保存定义）。

## 验证方式

* 新增单元测试/最小验证用例：

  * 输入 `['uuid']` 能按预期解析（走字符串数组分支）。

  * 输入 `[{"name":"x",...}]` 能按预期解析（走对象数组分支，若启用方案2则验证构造成功）。

* 对 `ToolVersionEntity` 的 MyBatis 映射做一次读库模拟（TypeHandler 生效）。

## 交付物

* TypeHandler/实体字段的修复（按最终确定的存储语义）。

* JSON 解析失败日志增强。

* 覆盖两种 JSON 形态的测试用例。

如果你希望我直接落地，我将默认按“方案1：兼容两种 JSON 形态 + 语义偏向ID列表（因为当前报错数据就是UUID字符串数组）”实施；后续如确认确实要存工具定义，再切换到方案2并提供迁移脚本建议。
