package com.sky.AgentCore.dto.user;

import com.sky.AgentCore.dto.model.FallbackConfig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/** 用户设置配置 */
@Data
public class UserSettingsConfig implements Serializable {

    /**
     * 默认聊天模型ID
     */
    private String defaultModel;

    /**
     * 默认OCR模型ID
     */
    private String defaultOcrModel;

    /**
     * 默认嵌入模型ID
     */
    private String defaultEmbeddingModel;

    /**
     * 降级配置
     */
    private FallbackConfig fallbackConfig;

    public UserSettingsConfig(String defaultModel,String defaultOcrModel, String defaultEmbeddingModel) {
        this.defaultModel = defaultModel;
        this.defaultOcrModel = defaultOcrModel;
        this.defaultEmbeddingModel = defaultEmbeddingModel;
    }

    public UserSettingsConfig() {
    }
}
