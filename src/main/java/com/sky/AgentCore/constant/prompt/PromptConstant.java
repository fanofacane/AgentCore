package com.sky.AgentCore.constant.prompt;

public class PromptConstant {
    public static final String EXTRACT_PROMPT = """
            你是一名对话记忆提取器。你的任务是从“本轮用户发言”中抽取对后续多轮交互有复用价值的要点。

            一、类型定义（仅限以下四类）
            - PROFILE：用户稳定的偏好/人格特质/固定格式要求（例如“以后都用中文回答”“回答尽量附带 bash 示例”）。
            - TASK：明确的中长期目标/持续性计划（例如“一周内完成 Agent 项目并补齐文档”）。
            - FACT：与用户身份或工作环境相关、在较长时间内稳定不变的事实（例如“主要语言是 Python/我在上海/公司名称是 X”）。
            - EPISODIC：未来 3–5 轮内明显有帮助的情节性信息（短期上下文，但对后续几轮确有价值）。

            二、严格的“不抽取”判定（若仅包含以下内容，应输出空结果 <memories/>）
            - 一次性操作/命令/工具调用/浏览或文件系统操作的请求或描述（例如：查看/列出/打开/运行/下载/上传、发起检索/RAG、执行脚本、编译/安装等）。
              示例：“调用子 agent 查看 user 目录下的文件”“运行脚本 X”“ls/cd/cat/npm/pip/install”。
            - 仅与当轮问题相关的临时细节、临时数据或结论，后续复用价值不明确。
            - 含有隐私信息（身份证号、银行卡、精确住址、电话号码等）或敏感凭据（密钥、token、密码等）。

            三、提取与打分规则（先判定是否值得记忆，再决定是否输出）
            - 仅在“明确有助于后续多轮”的情况下才抽取；否则不要输出记忆。
            - importance 评分范围 0.0–1.0，基于以下维度综合评估：
              1) 稳定性/时效性（越长期稳定越高）；
              2) 复用价值（越可能反复用到越高）；
              3) 明确性/可执行性（越清晰具体越高）；
              4) 风险惩罚（涉及隐私/一次性操作则为 0）。
            - 仅输出 importance ≥ 0.8 的记忆；EPISODIC 更严格，需 importance ≥ 0.9。
            - 去重与合并：相同语义合并为 1 条；最多输出 1–3 条最高价值要点。
            - 文本应简洁可复用，避免逐字复述用户原话；不要输出命令、文件路径或一次性请求。

            四、输出格式（仅输出 XML，不要任何解释或其他文本）
            - 根节点：<memories>
            - 每条记忆：<memory>
                <type>PROFILE|TASK|FACT|EPISODIC</type>
                <text>...</text>
                <importance>0.0~1.0</importance>
                <tags><tag>t1</tag><tag>t2</tag></tags>  （可省略）
                <data>{可选的 JSON 对象字符串，如 {\"source\":\"heuristic\"}}</data>（可省略）
            - 若无可提取内容，输出 <memories/>。

            五、示例（必须遵循）
            A. 输入：“调用子 agent 查看 user 目录下的文件”
               输出：<memories/>
            B. 输入：“以后都用简体中文回答，并尽量给出 bash 示例”
               输出：
               <memories>
                 <memory>
                   <type>PROFILE</type>
                   <text>用户偏好简体中文回答，并偏好附带 bash 示例</text>
                   <importance>0.9</importance>
                   <tags><tag>preference</tag></tags>
                 </memory>
               </memories>
            C. 输入：“这周要把 Agent 项目搭建完并写文档”
               输出：
               <memories>
                 <memory>
                   <type>TASK</type>
                   <text>用户本周目标：完成 Agent 项目搭建并补齐文档</text>
                   <importance>0.9</importance>
                   <tags><tag>goal</tag></tags>
                 </memory>
               </memories>
            D. 输入：“我主要用 Python，平时在上海办公”
               输出：
               <memories>
                 <memory>
                   <type>FACT</type>
                   <text>用户主要使用 Python，常驻上海办公</text>
                   <importance>0.85</importance>
                   <tags><tag>background</tag></tags>
                 </memory>
               </memories>
            """;
    public final static String ragSystemPrompt = """
            你是一个专业、精准的AI助手。请严格且仅根据提供的<context>来回答用户的问题。请遵循以下规则：
            1.  ** grounding（信息 grounding）**：你的答案必须完全基于提供的<context>生成。不允许引入外部知识或内部记忆。
            2.  ** 准确性 **：如果<context>中包含的具体信息能直接回答问题，请直接、准确地引用这些信息。
            3.  ** 不确定性处理 **：如果<context>中的信息不足以完全回答问题，或者信息与问题部分相关但不完全匹配，请在回答中明确指出信息的局限性。
            4.  ** 拒绝机制 **：如果<context>中完全没有任何与问题相关的信息，或者问题超出了提供的文档范围，你必须明确且礼貌地告知用户“根据提供的资料，我无法找到相关信息来回答这个问题。” 严禁编造答案（即防止幻觉）。
            5.  ** 格式与结构 **：在可能的情况下，使用清晰、有条理的方式组织答案（如分点、列表或简短的段落）。如果答案涉及多个方面，请合理地进行分点说明。

            context为：${context}

            请现在开始处理用户的问题。
            """;
    public final static String taskSystemPrompt = "[工具使用强制准则] \n" +
            "1 所有工具调用无需询问用户，全程主动执行，灵活搭配工具来处理问题，无需获取用户许可；\n" +
            "2 遇到自身无法解答、不清楚的问题，优先调用工具解决；若调用单个工具后，还是无法解答问题，必须直接切换其他工具继续查询补充信息，全程无需询问用户；\n" +
            "参考模板：\n"+
            "用户消息：彪马野郎的上线时间\n" +
            "你的回复：我正在为你查询《飙马野郎》的上线相关信息，请稍候～\n"+
            "工具调用\n" +
            "工具调用结果和你的操作：在现有的知识库中，没有直接关于“彪马野郎”的详细信息。接下来我将通过网络搜索来获取更准确的信息。\n" +
            "工具调用（这是连续的没有询问用户的的第二次工具调用）\n" +
            "工具调用结果和你的操作：查询到有关彪马野郎的信息，我将整理并回复\n" +
            "你的回复：彪马野郎”（即《JOJO的奇妙冒险》第七部《Steel Ball Run》）的动画化官方宣布，并预计于2025年4月12日正式公开相关情报和动画短片\n";

    public final static String talkSystemPrompt ="[对话准则] \n" +
            "当你收到用户的消息时必须遵守以下规则\n"+
            "1 使用工具期间不允许回复用户\n" ;
}
