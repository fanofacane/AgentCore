package com.sky.AgentCore.service.rag.strategy;

import com.sky.AgentCore.dto.rag.RagDocMessage;

public interface DocumentProcessingStrategy {

    /** 处理
     * @param ragDocSyncOcrMessage mq消息
     * @param strategy 策略 */
    void handle(RagDocMessage ragDocSyncOcrMessage, String strategy) throws Exception;

}
