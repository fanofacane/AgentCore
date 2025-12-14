package com.sky.AgentCore.service.product.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ProductAssembler;
import com.sky.AgentCore.dto.model.ModelEntity;
import com.sky.AgentCore.dto.model.ProviderEntity;
import com.sky.AgentCore.dto.product.ProductDTO;
import com.sky.AgentCore.dto.product.ProductEntity;
import com.sky.AgentCore.enums.BillingType;
import com.sky.AgentCore.mapper.ProductMapper;
import com.sky.AgentCore.service.llm.LLMAppService;
import com.sky.AgentCore.service.llm.LLMDomainService;
import com.sky.AgentCore.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private LLMDomainService llmDomainService;
    @Autowired
    private LLMAppService llmAppService;
    /**
     * 根据业务主键查找商品 这是计费系统的核心查询方法
     * @param type      计费类型
     * @param serviceId 业务ID
     * @return 商品实体，如果不存在则返回null
     */
    @Override
    public ProductEntity findProductByBusinessKey(String type, String serviceId) {
        if (type == null || type.trim().isEmpty()) return null;

        if (serviceId == null || serviceId.trim().isEmpty()) return null;

        BillingType billingType;
        try {
            billingType = BillingType.fromCode(type);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return lambdaQuery().eq(ProductEntity::getType, billingType)
                .eq(ProductEntity::getServiceId, serviceId).one();
    }

    /**
     * 根据ID获取商品
     *
     * @param productId 商品ID
     * @return 商品DTO
     */
    @Override
    public ProductDTO getProductById(String productId) {
        ProductEntity entity = getById(productId);
        if (entity == null) throw new BusinessException("商品不存在");
        return ProductAssembler.toDTO(entity);
    }

    /**
     * 根据业务主键获取商品
     *
     * @param type      计费类型
     * @param serviceId 业务ID
     * @return 商品DTO，如果不存在则返回null
     */
    @Override
    public ProductDTO getProductByBusinessKey(String type, String serviceId) {
        ProductEntity entity = findProductByBusinessKey(type, serviceId);
        return ProductAssembler.toDTO(entity);
    }

    /**
     * 获取激活的商品列表
     *
     * @param type 商品类型（可选）
     * @return 商品DTO列表
     */
    @Override
    public List<ProductDTO> getActiveProducts(String type) {
        List<ProductEntity> entities = getActiveProduct(type);
        List<ProductDTO> productDTOs = ProductAssembler.toDTOs(entities);

        // 为模型类型的商品填充模型信息
        enrichModelInfo(productDTOs);

        return productDTOs;
    }
    /** 检查商品是否存在且激活
     * @param type 计费类型
     * @param serviceId 业务ID
     * @return 是否存在且激活 */
    @Override
    public Boolean isProductActive(String type, String serviceId) {
        ProductEntity product = findProductByBusinessKey(type, serviceId);
        return product != null && product.isActive();
    }

    /** 为模型类型商品填充模型信息
     * @param productDTOs 商品DTO列表 */
    private void enrichModelInfo(List<ProductDTO> productDTOs) {
        // 筛选出模型类型的商品
        List<ProductDTO> modelProducts = productDTOs.stream()
                .filter(product -> "MODEL_USAGE".equals(product.getType())).toList();

        if (modelProducts.isEmpty()) return;

        // 收集所有需要查询的模型ID
        Set<String> modelIds = modelProducts.stream().map(ProductDTO::getServiceId)
                .collect(Collectors.toSet());

        try {
            // 批量查询模型信息
            List<ModelEntity> models = llmDomainService.getModelsByIds(modelIds);
            Map<String, ModelEntity> modelMap = models.stream()
                    .collect(Collectors.toMap(ModelEntity::getId, model -> model));

            // 收集需要查询的服务商ID
            Set<String> providerIds = models.stream().map(ModelEntity::getProviderId).collect(Collectors.toSet());

            // 批量查询服务商信息
            Map<String, ProviderEntity> providerMap = providerIds.stream()
                    .collect(Collectors.toMap(providerId -> providerId, providerId -> {
                        try {
                            return llmAppService.getById(providerId);
                        } catch (Exception e) {
                            return null; // 如果服务商不存在，返回null
                        }
                    }));

            // 为每个模型商品填充信息
            for (ProductDTO product : modelProducts) {
                ModelEntity model = modelMap.get(product.getServiceId());
                if (model != null) {
                    product.setModelName(model.getName());
                    product.setModelId(model.getModelId());
                    // 设置服务商名称
                    ProviderEntity provider = providerMap.get(model.getProviderId());
                    if (provider != null) product.setProviderName(provider.getName());
                }
            }
        } catch (Exception e) {
            // 如果查询模型信息失败，不影响商品查询，只是没有模型信息
            // 可以记录日志但不抛出异常
        }
    }
    /**
     * 获取指定类型的激活商品
     *
     * @param type 计费类型
     * @return 激活的商品列表
     */
    public List<ProductEntity> getActiveProduct(String type) {
        LambdaQueryWrapper<ProductEntity> wrapper = Wrappers.<ProductEntity>lambdaQuery()
                .eq(ProductEntity::getStatus, 1).orderByDesc(ProductEntity::getCreatedAt);

        if (type != null && !type.trim().isEmpty()) wrapper.eq(ProductEntity::getType, type);

        return productMapper.selectList(wrapper);
    }
}
