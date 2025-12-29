package com.sky.AgentCore.service.rule;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.AgentCore.dto.product.ProductDTO;
import com.sky.AgentCore.dto.product.ProductEntity;

import java.util.List;

public interface ProductService extends IService<ProductEntity> {
    ProductEntity findProductByBusinessKey(String type, String serviceId);

    ProductDTO getProductById(String productId);

    ProductDTO getProductByBusinessKey(String type, String serviceId);

    List<ProductDTO> getActiveProducts(String type);

    Boolean isProductActive(String type, String serviceId);
}
