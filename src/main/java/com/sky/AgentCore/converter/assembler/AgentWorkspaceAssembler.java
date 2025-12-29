package com.sky.AgentCore.converter.assembler;

import com.sky.AgentCore.dto.model.LLMModelConfig;
import com.sky.AgentCore.dto.model.UpdateModelConfigRequest;
import org.springframework.beans.BeanUtils;

/** Agent领域对象组装器 负责DTO、Entity和Request之间的转换 */
public class AgentWorkspaceAssembler {

    public static LLMModelConfig toLLMModelConfig(UpdateModelConfigRequest request) {

        LLMModelConfig llmModelConfig = new LLMModelConfig();
        BeanUtils.copyProperties(request, llmModelConfig);
        return llmModelConfig;
    }

}
