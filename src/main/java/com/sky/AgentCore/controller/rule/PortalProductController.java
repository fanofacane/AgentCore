package com.sky.AgentCore.controller.rule;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.dto.product.ProductDTO;
import com.sky.AgentCore.service.rule.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 门户商品控制器 负责处理用户对计费商品的查询操作 */
@RestController
@RequestMapping("/products")
public class PortalProductController {
    @Autowired
    private ProductService productService;
    /** 根据ID获取商品详情
     * @param productId 商品ID
     * @return 商品详情 */
    @GetMapping("/{productId}")
    public Result<ProductDTO> getProductById(@PathVariable String productId) {
        return Result.success(productService.getProductById(productId));
    }
    /** 根据业务标识获取商品
     *
     * @param type 计费类型
     * @param serviceId 服务ID
     * @return 商品详情 */
    @GetMapping("/business")
    public Result<ProductDTO> getProductByBusinessKey(@RequestParam String type, @RequestParam String serviceId) {
        return Result.success(productService.getProductByBusinessKey(type, serviceId));
    }

    /** 获取指定类型的活跃商品列表
     *
     * @param type 计费类型（可选）
     * @return 活跃商品列表 */
    @GetMapping("/active")
    public Result<List<ProductDTO>> getActiveProducts(@RequestParam(required = false) String type) {
        return Result.success(productService.getActiveProducts(type));
    }

    /** 检查商品是否存在且激活
     *
     * @param type 计费类型
     * @param serviceId 服务ID
     * @return 是否存在且激活 */
    @GetMapping("/business/active")
    public Result<Boolean> isProductActive(@RequestParam String type, @RequestParam String serviceId) {
        return Result.success(productService.isProductActive(type, serviceId));
    }
}
