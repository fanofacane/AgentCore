package com.sky.AgentCore.service.agent.Impl;


import com.sky.AgentCore.dto.agent.AgentConfig;
import com.sky.AgentCore.dto.tool.ToolConfig;
import com.sky.AgentCore.enums.ProviderProtocol;
import com.sky.AgentCore.service.agent.Agent;
import com.sky.AgentCore.service.agent.AgentConfigService;
import com.sky.AgentCore.service.tool.ToolConfigService;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.extern.slf4j.Slf4j;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@Service
public class AgentBuildConfig {
    @Autowired
    private AgentConfigService agentConfigService;
    @Autowired
    private ToolConfigService toolConfigService;

    public AgentConfig buildAgent(String agentId){
        AgentConfig agentConfig = agentConfigService.getByAgentId(agentId);
        List<String> toolIds = Arrays.stream(agentConfig.getToolIds().split(",")).map(String::trim).collect(Collectors.toList());
        List<ToolConfig> toolConfigList  = toolConfigService.findByToolIdInAndIsEnabledTrue(toolIds);
        return agentConfig;
    }
    /**
     * 构建配置后的ChatModel（覆盖温度、maxTokens等参数）
     */
    public String buildConfiguredChatModel(AgentConfig agentConfig,String message) {
        String apikey="sk-UYEXVUWKd67E9d8e9171T3BlBkFJf4C09a1Ca9b44cCE9f24";
        String baseUrl="https://apic1.ohmycdn.com/v1";
        String modelName="gpt-5-nano";
        String url="http://115.190.126.170:7000/tongyi-wanxiang";
        StreamingChatModel model = new OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder().apiKey(apikey)
                    .baseUrl(baseUrl).modelName(modelName).timeout(Duration.ofHours(1)).build();

        McpTransport transport = new HttpMcpTransport.Builder().sseUrl(url).logRequests(true).logResponses(true)
                .timeout(Duration.ofHours(1)).build();

        McpClient mcpClient = new DefaultMcpClient.Builder().transport(transport).build();

        McpToolProvider toolProvider = McpToolProvider.builder().mcpClients(mcpClient).build();

        AiServices<Agent> agentService = AiServices.builder(Agent.class).streamingChatModel(model);

        if (toolProvider != null) agentService.toolProvider(toolProvider);

        Agent agent = agentService.build();
        agent.chat(message);
        return apikey;
    }

}
