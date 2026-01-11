package com.sky.AgentCore.service.tool.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.dto.tool.*;
import com.sky.AgentCore.mapper.tool.ToolMapper;
import com.sky.AgentCore.service.tool.ToolService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, ToolEntity> implements ToolService {
    @Override
    public Page<ToolDTO> getEnableTools(QueryToolRequest queryToolRequest) {
        // 1. 构建查询条件
        LambdaQueryWrapper<ToolEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ToolEntity::getStatus, true);
        if (queryToolRequest.getKeyword() != null && !queryToolRequest.getKeyword().isEmpty()) {
            wrapper.like(ToolEntity::getName, queryToolRequest.getToolName());
        }
        wrapper.orderByDesc(ToolEntity::getCreatedAt);

        // 2. 新建一个 泛型为ToolEntity 的分页对象，解决泛型不匹配问题
        Page<ToolEntity> toolEntityPage = new Page<>(queryToolRequest.getPage(), queryToolRequest.getPageSize());
        // 调用父类的分页方法，查询数据库，返回 数据库实体 的分页数据
        Page<ToolEntity> resultPage = this.page(toolEntityPage, wrapper);
        List<ToolDTO> dtoList = resultPage.getRecords().stream()
                .map(entity -> {
                    // 初始化前端需要的DTO对象
                    ToolDTO dto = new ToolDTO();
                    // 属性拷贝
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                }).collect(Collectors.toList());
        Page<ToolDTO> page = new Page<>(queryToolRequest.getPage(), queryToolRequest.getPageSize());
        page.setRecords(dtoList);
        return page;
    }

    @Override
    public List<ToolEntity> getAllEnableTools(List<String> toolIds){
        return lambdaQuery().eq(ToolEntity::getStatus, true)
                .in(ToolEntity::getId,toolIds)
                .list();
    }
}
