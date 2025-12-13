package com.sky.AgentCore.service.agent.Impl;


import com.sky.AgentCore.dto.agent.AgentConfig;
import com.sky.AgentCore.dto.tool.ToolConfig;
import com.sky.AgentCore.service.agent.AgentConfigService;
import com.sky.AgentCore.service.tool.ToolConfigService;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
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
        // 基于默认模型，设置数据库中的参数
        OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder =new OpenAiChatModel.OpenAiChatModelBuilder();
        openAiChatModelBuilder.apiKey("sk-UYEXVUWKd67E9d8e9171T3BlBkFJf4C09a1Ca9b44cCE9f24").modelName("gpt-5-nano")
                .baseUrl("https://apic1.ohmycdn.com/v1")
                .timeout(Duration.ofSeconds(10)).build();
        ChatModel chatModel=new OpenAiChatModel(openAiChatModelBuilder);
        SystemMessage systemMessage =new SystemMessage(agentConfig.getRoleDesc());
        UserMessage userMessage =new UserMessage(message);
        ChatResponse response = chatModel.chat(Arrays.asList(systemMessage, userMessage));
        // 返回配置后的模型（不影响全局默认模型）
        return  response.aiMessage().text();
    }

}
