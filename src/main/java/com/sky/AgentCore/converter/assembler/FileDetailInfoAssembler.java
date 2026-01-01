package com.sky.AgentCore.converter.assembler;

import com.sky.AgentCore.dto.rag.FileDetailEntity;
import com.sky.AgentCore.dto.rag.FileDetailInfoDTO;
import org.springframework.beans.BeanUtils;

/** 文件详细信息转换器 */
public class FileDetailInfoAssembler {

    /** Convert FileDetailEntity to FileDetailInfoDTO */
    public static FileDetailInfoDTO toDTO(FileDetailEntity entity) {
        if (entity == null) {
            return null;
        }
        FileDetailInfoDTO dto = new FileDetailInfoDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setFileId(entity.getId());
        return dto;
    }
}
