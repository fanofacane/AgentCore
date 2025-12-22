package com.sky.AgentCore.dto.tool;

public class ToolOperationResult {

    private final ToolEntity tool;
    private final boolean needStateTransition;

    private ToolOperationResult(ToolEntity tool, boolean needStateTransition) {
        this.tool = tool;
        this.needStateTransition = needStateTransition;
    }

    public static ToolOperationResult of(ToolEntity tool, boolean needStateTransition) {
        return new ToolOperationResult(tool, needStateTransition);
    }

    public static ToolOperationResult withoutTransition(ToolEntity tool) {
        return new ToolOperationResult(tool, false);
    }

    public static ToolOperationResult withTransition(ToolEntity tool) {
        return new ToolOperationResult(tool, true);
    }

    public ToolEntity getTool() {
        return tool;
    }

    public boolean needStateTransition() {
        return needStateTransition;
    }
}
