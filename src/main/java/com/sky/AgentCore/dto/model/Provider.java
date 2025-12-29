package com.sky.AgentCore.dto.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sky.AgentCore.config.Exceptions.BusinessException;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.dto.enums.ProviderProtocol;

import java.util.Objects;

public class Provider extends BaseEntity {

        @TableId(type = IdType.ASSIGN_UUID)
        private String id;

        private String userId;

        private ProviderProtocol protocol;
        private String name;
        private String description;

        private ProviderConfig config;

        private Boolean isOfficial;
        private Boolean status;

        public void setConfig(ProviderConfig config) {
            this.config = config;
        }

        public ProviderConfig getConfig() {
            return config;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ProviderProtocol getProtocol() {
            return protocol;
        }

        public void setProtocol(ProviderProtocol protocol) {
            this.protocol = protocol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getIsOfficial() {
            return isOfficial;
        }

        public void setIsOfficial(Boolean official) {
            isOfficial = official;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public void isActive() {
            if (!status) {
                throw new BusinessException("服务商未激活");
            }
        }

        public void isAvailable(String userId) {
            if (!isOfficial && !Objects.equals(this.getUserId(), userId)) {
                throw new BusinessException("模型未找到");
            }
        }
}
