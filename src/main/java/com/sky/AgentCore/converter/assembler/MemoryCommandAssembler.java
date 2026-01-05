package com.sky.AgentCore.converter.assembler;

import com.sky.AgentCore.dto.memory.CandidateMemory;
import com.sky.AgentCore.dto.memory.CreateMemoryRequest;
import com.sky.AgentCore.enums.MemoryType;

public class MemoryCommandAssembler {

    public static CandidateMemory toCandidate(CreateMemoryRequest req) {
        CandidateMemory cm = new CandidateMemory();
        cm.setType(MemoryType.safeOf(req.getType()));
        cm.setText(req.getText());
        cm.setImportance(req.getImportance());
        cm.setTags(req.getTags());
        cm.setData(req.getData());
        return cm;
    }
}

