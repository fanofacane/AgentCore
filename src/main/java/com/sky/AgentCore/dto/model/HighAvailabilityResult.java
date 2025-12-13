package com.sky.AgentCore.dto.model;


import lombok.Data;

/** 高可用选择结果
 * @since 1.0.0 */
@Data
public class HighAvailabilityResult {

    /** 选择的Provider */
    private ProviderEntity provider;

    /** 选择的Model（可能有不同的部署名称） */
    private ModelEntity model;

    /** 实例ID（用于结果上报） */
    private String instanceId;

    /** 模型是否被切换（降级到备用模型） */
    private boolean switched;
    public HighAvailabilityResult() {
    }

    public HighAvailabilityResult(ProviderEntity provider, ModelEntity model, String instanceId) {
        this.provider = provider;
        this.model = model;
        this.instanceId = instanceId;
        this.switched = false;
    }

    public HighAvailabilityResult(ProviderEntity provider, ModelEntity model, String instanceId, boolean switched) {
        this.provider = provider;
        this.model = model;
        this.instanceId = instanceId;
        this.switched = switched;
    }
}
