package com.sky.AgentCore.service.memory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.AgentCore.converter.assembler.MemoryAssembler;
import com.sky.AgentCore.converter.assembler.MemoryCommandAssembler;
import com.sky.AgentCore.dto.memory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryAppService {
    @Autowired
    private MemoryDomainService memoryDomainService;
    /** 分页列出用户记忆 */
    public Page<MemoryItemDTO> listUserMemories(String userId, QueryMemoryRequest request) {
        int pageNo = request.getPage() != null ? request.getPage() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
        Page<MemoryItemEntity> page = memoryDomainService.pageMemories(userId, request.getType(), pageNo, pageSize);
        Page<MemoryItemDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(MemoryAssembler.toDTOs(page.getRecords()));
        return dtoPage;
    }

    public void createMemory(String userId, CreateMemoryRequest request) {
        CandidateMemory cm = MemoryCommandAssembler.toCandidate(request);
        List<CandidateMemory> list = new ArrayList<>();
        list.add(cm);
        memoryDomainService.saveMemories(userId, null, list);
    }

    /** 归档（软删除）记忆 */
    public boolean deleteMemory(String userId, String itemId) {
        return memoryDomainService.delete(userId, itemId);
    }
}
