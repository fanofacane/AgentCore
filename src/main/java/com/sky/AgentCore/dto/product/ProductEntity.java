package com.sky.AgentCore.dto.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.BillingTypeConverter;
import com.sky.AgentCore.converter.PricingConfigConverter;
import com.sky.AgentCore.dto.common.BaseEntity;
import com.sky.AgentCore.enums.BillingType;
import lombok.Data;

import java.util.Map;

/** 商品实体 定义可计费项，关联业务，存储价格配置 */
@TableName(value = "products", autoResultMap = true)
@Data
public class ProductEntity extends BaseEntity {

    /** 商品唯一ID */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /** 商品名称 */
    @TableField("name")
    private String name;

    /** 商品类型 (MODEL_USAGE, AGENT_CREATION等) */
    @TableField(value = "type", typeHandler = BillingTypeConverter.class)
    private BillingType type;

    /** 关联的业务ID */
    @TableField("service_id")
    private String serviceId;

    /** 关联的规则ID */
    @TableField("rule_id")
    private String ruleId;

    /** 价格配置 JSON格式 */
    @TableField(value = "pricing_config", typeHandler = PricingConfigConverter.class)
    private Map<String, Object> pricingConfig;

    /** 状态 1-激活 0-禁用 */
    @TableField("status")
    private Integer status;

    /** 检查商品是否激活 */
    public boolean isActive() {
        return Integer.valueOf(1).equals(status);
    }

    /** 激活商品 */
    public void activate() {
        this.status = 1;
    }

    /** 禁用商品 */
    public void deactivate() {
        this.status = 0;
    }

    /** 更新状态 */
    public void updateStatus(Integer newStatus) {
        if (newStatus == null || (newStatus != 0 && newStatus != 1)) {
            throw new BusinessException("商品状态只能为0(禁用)或1(激活)");
        }
        this.status = newStatus;
    }

    /** 验证价格配置 */
    public void validatePricingConfig() {
        if (pricingConfig == null || pricingConfig.isEmpty()) {
            throw new BusinessException("商品价格配置不能为空");
        }
    }

    /** 验证商品基本信息 */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("商品名称不能为空");
        }
        if (type == null) {
            throw new BusinessException("商品类型不能为空");
        }
        if (serviceId == null || serviceId.trim().isEmpty()) {
            throw new BusinessException("业务ID不能为空");
        }
        if (ruleId == null || ruleId.trim().isEmpty()) {
            throw new BusinessException("规则ID不能为空");
        }
        validatePricingConfig();
    }
}
