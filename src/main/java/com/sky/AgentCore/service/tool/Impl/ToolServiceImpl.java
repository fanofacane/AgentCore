package com.sky.AgentCore.service.tool.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.tool.ToolEntity;
import com.sky.AgentCore.dto.tool.UserToolEntity;
import com.sky.AgentCore.mapper.ToolMapper;
import com.sky.AgentCore.mapper.UserToolMapper;
import com.sky.AgentCore.service.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
