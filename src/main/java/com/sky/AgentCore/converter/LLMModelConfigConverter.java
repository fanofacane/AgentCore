package com.sky.AgentCore.converter;


import com.sky.AgentCore.dto.LLMModelConfig;
import org.apache.ibatis.type.MappedTypes;

/** LLMModelConfig JSON转换器 */
@MappedTypes(LLMModelConfig.class)
public class LLMModelConfigConverter extends JsonToStringConverter<LLMModelConfig> {

    public LLMModelConfigConverter() {
        super(LLMModelConfig.class);
    }
}
