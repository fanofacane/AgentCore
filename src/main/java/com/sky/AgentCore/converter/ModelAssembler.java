package com.sky.AgentCore.converter;


import com.sky.AgentCore.dto.model.ModelDTO;
import com.sky.AgentCore.dto.model.ModelEntity;

/** 模型对象转换器 */
public class ModelAssembler {

    /** 将领域对象转换为DTO */
    public static ModelDTO toDTO(ModelEntity model) {
        if (model == null) {
            return null;
        }

        ModelDTO dto = new ModelDTO();
        dto.setId(model.getId());
        dto.setUserId(model.getUserId());
        dto.setProviderId(model.getProviderId());
        dto.setModelId(model.getModelId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setType(model.getType());
        dto.setStatus(model.getStatus());
        dto.setModelEndpoint(model.getModelEndpoint());
        dto.setCreatedAt(model.getCreatedAt());
        dto.setUpdatedAt(model.getUpdatedAt());
        dto.setIsOfficial(model.getIsOfficial());
        return dto;
    }

    /** 将领域对象转换为DTO，并设置服务商名称 */
    public static ModelDTO toDTO(ModelEntity model, String providerName) {
        ModelDTO dto = toDTO(model);
        if (dto != null) {
            dto.setProviderName(providerName);
        }
        return dto;
    }

}
