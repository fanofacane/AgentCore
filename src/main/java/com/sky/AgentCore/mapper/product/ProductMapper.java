package com.sky.AgentCore.mapper.product;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.AgentCore.dto.product.ProductEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
}
