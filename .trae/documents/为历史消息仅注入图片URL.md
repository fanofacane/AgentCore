## 变更概述
- 在 AbstractMessageHandler 的 buildHistoryMessage 方法中，为 memory.add(UserMessage.from(ImageContent.from(fileUrl))) 增加图片格式校验，仅当 URL 指向图片格式时才注入。
- 兼容常见图片扩展名与 data:image/* 内联数据，忽略非图片 URL，保持文本消息逻辑不变。

## 修改位置
- 文件：[AbstractMessageHandler.java](file:///d:/software/AgentCore/src/main/java/com/sky/AgentCore/service/chat/handler/AbstractMessageHandler.java#L682-L689)

## 实现步骤
- 新增私有方法 isImageUrl(String url)：
  - 返回 true 当满足以下任一条件：
    - url 以 data:image/ 开头（base64 内联图片）。
    - 小写化后匹配常见图片扩展名：jpg、jpeg、png、gif、bmp、webp（允许带查询参数）。
  - 空值与空字符串直接返回 false。
- 在遍历 fileUrls 时：
  - 对每个 fileUrl 执行 isImageUrl 检查；仅当为图片时执行 memory.add(UserMessage.from(ImageContent.from(fileUrl)))；否则跳过。
  - 可加入 debug 日志记录被跳过的非图片 URL，便于排查。
- 不改动现有用户文本与 AI/System 消息注入逻辑，保证向后兼容。

## 验证方案
- 构造历史消息包含：
  - 图片 URL（如 https://a/b.png）→ 应被注入为 ImageContent。
  - 非图片 URL（如 https://a/b.pdf、https://a/b.txt）→ 不应注入 ImageContent。
  - data:image/png;base64,... → 应被注入。
  - 带查询参数的图片 URL（如 https://a/b.jpg?x=1）→ 应被注入。
- 运行一次包含上述场景的会话，检查 memory 中消息序列，仅含图片视觉内容与文本内容。

## 设计考虑
- 优先使用扩展名白名单与 data:image/ 简易判定，避免在历史消息构建阶段引入网络 HEAD/下载和 MIME 检测的性能与稳定性成本。
- 若后续需要更严谨的 MIME 校验，可在上传/存储环节统一做类型判定，再在历史注入中只信任受管 URL。
