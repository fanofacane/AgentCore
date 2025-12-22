package com.sky.AgentCore.dto.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/** 降级配置 */
@Data
public class FallbackConfig implements Serializable {
    /** 是否启用降级 */
    private boolean enabled = false;

    /** 降级链，按优先级排序的模型ID列表 */
    private List<String> fallbackChain = new ArrayList<>();
    /** 添加降级模型到链中 */
    public void addFallbackModel(String modelId) {
        if (modelId != null && !fallbackChain.contains(modelId)) {
            fallbackChain.add(modelId);
        }
    }

    /** 移除降级模型 */
    public void removeFallbackModel(String modelId) {
        fallbackChain.remove(modelId);
    }
}
