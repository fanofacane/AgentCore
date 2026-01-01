package com.sky.AgentCore.service.chat.handler;


import com.sky.AgentCore.config.Factory.LLMServiceFactory;
import com.sky.AgentCore.dto.chat.ChatContext;
import com.sky.AgentCore.service.agent.SessionService;
import com.sky.AgentCore.service.billing.BillingService;
import com.sky.AgentCore.service.chat.Impl.ChatSessionManager;
import com.sky.AgentCore.service.chat.MessageService;
import com.sky.AgentCore.service.gateway.HighAvailabilityService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.tool.AgentToolManager;
import com.sky.AgentCore.service.tool.builtin.BuiltInToolRegistry;
import com.sky.AgentCore.service.trace.TraceCollector;
import com.sky.AgentCore.service.user.AccountAppService;
import com.sky.AgentCore.service.user.UserSettingsDomainService;

import dev.langchain4j.service.tool.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Agent消息处理器 用于支持工具调用的对话模式 实现任务拆分、执行和结果汇总的工作流 使用事件驱动架构进行状态转换 */
@Component(value = "agentMessageHandler")
public class AgentMessageHandler extends TracingMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(AgentMessageHandler.class);
    private AgentToolManager agentToolManager;

    public AgentMessageHandler(LLMServiceFactory llmServiceFactory, MessageService messageDomainService,
                               HighAvailabilityService highAvailabilityService, SessionService sessionService,
                               UserSettingsDomainService userSettingsDomainService, LLMDomainService llmDomainService,
                               BuiltInToolRegistry builtInToolRegistry, BillingService billingService,
                               AccountAppService accountService, ChatSessionManager chatSessionManager,
                               TraceCollector traceCollector, AgentToolManager agentToolManager) {
        super(llmServiceFactory, messageDomainService,userSettingsDomainService,billingService,
                llmDomainService, builtInToolRegistry,sessionService,
                accountService,chatSessionManager, highAvailabilityService,traceCollector);
        this.agentToolManager = agentToolManager;
    }

    @Override
    protected ToolProvider provideTools(ChatContext chatContext) {
        // todo 传递用户ID给工具管理器
/*        return agentToolManager.createToolProvider(agentToolManager.getAvailableTools(chatContext),
                chatContext.getAgent().getToolPresetParams(), chatContext.getUserId()); // 新增：传递用户ID*/

        return null;
    }
}
