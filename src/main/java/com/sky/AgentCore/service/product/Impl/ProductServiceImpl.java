package com.sky.AgentCore.service.product.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.product.ProductEntity;
import com.sky.AgentCore.enums.BillingType;
import com.sky.AgentCore.mapper.ProductMapper;
import com.sky.AgentCore.service.product.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductService {
    /** 根据业务主键查找商品 这是计费系统的核心查询方法
     * @param type 计费类型
     * @param serviceId 业务ID
     * @return 商品实体，如果不存在则返回null */
    @Override
    public ProductEntity findProductByBusinessKey(String type, String serviceId) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        if (serviceId == null || serviceId.trim().isEmpty()) {
            return null;
        }

        BillingType billingType;
        try {
            billingType = BillingType.fromCode(type);
        } catch (IllegalArgumentException e) {
            return null;
        }

         return lambdaQuery().eq(ProductEntity::getType, billingType)
                 .eq(ProductEntity::getServiceId, serviceId).one();
    }
}
