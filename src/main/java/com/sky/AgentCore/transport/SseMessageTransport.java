package com.sky.AgentCore.transport;

import com.sky.AgentCore.dto.agent.AgentChatResponse;
import com.sky.AgentCore.utils.SseEmitterUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class SseMessageTransport implements MessageTransport<SseEmitter> {

    private static final String TIMEOUT_MESSAGE = "\n\n[系统提示：响应超时，请重试]";

    private static final String ERROR_MESSAGE_PREFIX = "\n\n[系统错误：";

    @Override
    public SseEmitter createConnection(long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);

        emitter.onTimeout(() -> {
            try {
                AgentChatResponse response = new AgentChatResponse();
                response.setContent(TIMEOUT_MESSAGE);
                response.setDone(true);
                emitter.send(response);
                emitter.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        emitter.onError(ex -> {
            try {
                AgentChatResponse response = new AgentChatResponse();
                response.setContent(ERROR_MESSAGE_PREFIX + ex.getMessage() + "]");
                response.setDone(true);
                emitter.send(response);
                emitter.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return emitter;
    }

    @Override
    public void sendMessage(SseEmitter connection, AgentChatResponse streamChatResponse) {
        if (connection == null) {
            return;
        }
        SseEmitterUtils.safeSend(connection, streamChatResponse);
    }

    @Override
    public void sendEndMessage(SseEmitter connection, AgentChatResponse streamChatResponse) {
        if (connection == null) {
            return;
        }
        try {
            SseEmitterUtils.safeSend(connection, streamChatResponse);
        } finally {
            SseEmitterUtils.safeComplete(connection);
        }
    }

    @Override
    public void completeConnection(SseEmitter connection) {
        connection.complete();
    }

    @Override
    public void handleError(SseEmitter connection, Throwable error) {
        try {
            AgentChatResponse response = new AgentChatResponse();
            response.setContent(error.getMessage());
            response.setDone(true);
            connection.send(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            connection.complete();
        }
    }
}
