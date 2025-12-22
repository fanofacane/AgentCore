package com.sky.AgentCore.dto.model;

/** 模型更新事件
 *
 * @author fanofacane
 * @since 1.0.0 */
public class ModelUpdatedEvent extends ModelDomainEvent {

    /** 更新后的模型实体 */
    private final ModelEntity model;

    public ModelUpdatedEvent(String modelId, String userId, ModelEntity model) {
        super(modelId, userId);
        this.model = model;
    }

    public ModelEntity getModel() {
        return model;
    }
}
