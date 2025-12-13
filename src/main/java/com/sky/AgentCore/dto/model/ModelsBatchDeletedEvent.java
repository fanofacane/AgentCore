package com.sky.AgentCore.dto.model;

import lombok.Data;

import java.util.List;
/** 模型批量删除事件*/
@Data
public class ModelsBatchDeletedEvent {

    /**
     * 删除项列表
     */
    private final List<ModelDeleteItem> deleteItems;

    /**
     * 用户ID
     */
    private final String userId;

    /**
     * 模型删除项
     */
    public static class ModelDeleteItem {
        private final String modelId;
        private final String userId;

        public ModelDeleteItem(String modelId, String userId) {
            this.modelId = modelId;
            this.userId = userId;
        }
    }
}
