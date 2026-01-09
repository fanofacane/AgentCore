package com.sky.AgentCore.config;

import com.sky.AgentCore.enums.FileTypeEnum;
import com.sky.AgentCore.service.file.strategy.FileStorageStrategy;
import com.sky.AgentCore.service.file.strategy.Impl.AvatarFileStorageStrategy;
import com.sky.AgentCore.service.file.strategy.Impl.GeneralFileStorageStrategy;
import com.sky.AgentCore.service.file.strategy.Impl.RagFileStorageStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/** 文件存储配置类
 * 配置策略映射关系，供策略工厂使用
 */
@Configuration
public class FileStorageConfig {

    /** 配置文件类型与策略的映射关系
     *
     * @param ragFileStorageStrategy RAG文件策略
     * @param avatarFileStorageStrategy 头像文件策略
     * @param generalFileStorageStrategy 通用文件策略
     * @return 策略映射Map */
    @Bean
    public Map<FileTypeEnum, FileStorageStrategy> fileStorageStrategyMap(
            RagFileStorageStrategy ragFileStorageStrategy,
            AvatarFileStorageStrategy avatarFileStorageStrategy,
            GeneralFileStorageStrategy generalFileStorageStrategy) {

        Map<FileTypeEnum, FileStorageStrategy> strategyMap = new HashMap<>();

        strategyMap.put(FileTypeEnum.RAG, ragFileStorageStrategy);
        strategyMap.put(FileTypeEnum.AVATAR, avatarFileStorageStrategy);
        strategyMap.put(FileTypeEnum.GENERAL, generalFileStorageStrategy);

        return strategyMap;
    }
}
