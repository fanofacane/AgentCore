package com.sky.AgentCore.service.tool.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.AgentCore.Exceptions.BusinessException;
import com.sky.AgentCore.dto.tool.UserToolEntity;
import com.sky.AgentCore.mapper.UserToolMapper;
import com.sky.AgentCore.service.tool.UserToolService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserToolServiceImpl extends ServiceImpl<UserToolMapper, UserToolEntity> implements UserToolService {
    /** 检查工具版本是否已安装
     *
     * @param toolIds 工具版本id列表
     * @param userId 用户id */
    @Override
    public List<UserToolEntity> getInstallTool(List<String> toolIds, String userId) {
        if (toolIds == null || toolIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserToolEntity> userToolEntities = lambdaQuery()
                .in(UserToolEntity::getToolId, toolIds).eq(UserToolEntity::getUserId, userId).list();

        Map<String, UserToolEntity> userToolMap = userToolEntities.stream()
                .collect(Collectors.toMap(UserToolEntity::getToolId, Function.identity()));

        toolIds.forEach(toolId -> {
            UserToolEntity userToolEntity = userToolMap.get(toolId);
            if (userToolEntity == null) {
                throw new BusinessException("使用的工具不存在");
            }
        });
        return userToolEntities;
    }
}
