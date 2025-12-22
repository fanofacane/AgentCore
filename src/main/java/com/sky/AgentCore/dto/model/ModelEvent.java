package com.sky.AgentCore.dto.model;

public class ModelEvent extends ModelDomainEvent {

    /** 模型实体 */
    private final ModelEntity model;

    public ModelEvent(String modelId, String userId, ModelEntity model) {
        super(modelId, userId);
        this.model = model;
    }
    public ModelEntity getModel() {
        return model;
    }
}
