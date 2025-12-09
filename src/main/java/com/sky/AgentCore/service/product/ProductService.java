package com.sky.AgentCore.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.product.ProductEntity;

public interface ProductService extends IService<ProductEntity> {
    ProductEntity findProductByBusinessKey(String type, String serviceId);
}
