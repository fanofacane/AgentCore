package com.sky.AgentCore.service.rag.domain;

import com.sky.AgentCore.dto.rag.UserRagFileEntity;
import com.sky.AgentCore.mapper.UserRagFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRagFileDomainService {
    @Autowired
    private UserRagFileMapper userRagFileMapper;

    public UserRagFileEntity getById(String id) {
        return userRagFileMapper.selectById(id);
    }
}
