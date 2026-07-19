package com.company.scopery.modules.aiagent.tool.application.port;

import java.util.Map;

public interface AiToolHandler {
    String toolCode();
    String toolVersion();
    boolean readOnly();
    AiToolResult execute(Map<String, Object> arguments, AiToolExecutionContext context);
}
