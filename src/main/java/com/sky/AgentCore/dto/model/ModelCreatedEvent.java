package com.sky.AgentCore.dto.model;

/** 模型创建事件
 *
 * @author fanofacane
 * @since 1.0.0 */
public class ModelCreatedEvent extends ModelDomainEvent {

    /** 模型实体 */
    private final ModelEntity model;

    public ModelCreatedEvent(String modelId, String userId, ModelEntity model) {
        super(modelId, userId);
        this.model = model;
    }

    public ModelEntity getModel() {
        return model;
    }
}
