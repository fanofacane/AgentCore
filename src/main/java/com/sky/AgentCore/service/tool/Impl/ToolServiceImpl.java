package com.sky.AgentCore.service.tool.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.converter.ToolAssembler;
import com.sky.AgentCore.dto.tool.*;
import com.sky.AgentCore.enums.ToolStatus;
import com.sky.AgentCore.mapper.ToolMapper;
import com.sky.AgentCore.mapper.UserToolMapper;
import com.sky.AgentCore.service.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ToolServiceImpl extends ServiceImpl<ToolMapper, ToolEntity> implements ToolService {
    @Autowired
    private UserToolMapper userToolMapper;
    @Override
    public List<ToolEntity> getByIds(List<String> toolIds) {
        if (toolIds == null || toolIds.isEmpty()) return new ArrayList<>();
        return getByIds(toolIds);
    }

    @Override
    public UserToolEntity findByToolIdAndUserId(String toolId, String userId) {
        LambdaQueryWrapper<UserToolEntity> wrapper = Wrappers.<UserToolEntity>lambdaQuery()
                .eq(UserToolEntity::getToolId, toolId).eq(UserToolEntity::getUserId, userId);
        return userToolMapper.selectOne(wrapper);
    }


    /** 上传工具
     * 业务流程： 1. 将请求转换为实体 2. 调用领域服务创建工具 3. 将实体转换为DTO返回
     * @param request 创建工具请求
     * @param userId 用户ID
     * @return 创建的工具DTO */
/*    @Override
    @Transactional
    public ToolDTO uploadTool(CreateToolRequest request, String userId) {
        // 将请求转换为实体
        ToolEntity toolEntity = ToolAssembler.toEntity(request, userId);

        toolEntity.setStatus(ToolStatus.WAITING_REVIEW);
        // 调用领域服务创建工具
        ToolOperationResult result = toolDomainService.createTool(toolEntity);

        // 检查是否需要状态转换
        if (result.needStateTransition()) {
            toolStateStateMachine.submitToolForProcessing(result.getTool());
        }

        // 将实体转换为DTO返回
        return ToolAssembler.toDTO(result.getTool());
    }*/
}
