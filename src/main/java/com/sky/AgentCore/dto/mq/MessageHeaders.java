package com.sky.AgentCore.dto.mq;

/** Common header keys for messaging. */
public final class MessageHeaders {

    private MessageHeaders() {
    }

    /** Trace id header used across the system. */
    public static final String TRACE_ID = "seqId";
}

