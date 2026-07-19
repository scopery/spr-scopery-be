package com.company.scopery.modules.aiagent.tool.application.port;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AiToolHandlerRegistry {

    private static final Logger log = LoggerFactory.getLogger(AiToolHandlerRegistry.class);

    // Phase 42 read-only allowlist — mutation tools are never callable
    private static final Set<String> PHASE_42_ALLOWLIST = Set.of("knowledge.search");

    private final Map<String, AiToolHandler> handlers;

    public AiToolHandlerRegistry(List<AiToolHandler> handlerList) {
        Map<String, AiToolHandler> map = handlerList.stream()
                .collect(Collectors.toMap(
                        AiToolHandler::toolCode,
                        h -> h,
                        (a, b) -> {
                            throw new IllegalStateException(
                                    "Duplicate AiToolHandler registered for toolCode: " + a.toolCode());
                        }
                ));
        this.handlers = map;
        log.info("[AiToolHandlerRegistry] Registered {} live tool handlers: {}",
                handlers.size(), handlers.keySet());
    }

    public boolean isKnown(String toolCode) {
        return handlers.containsKey(toolCode);
    }

    public boolean isReadOnly(String toolCode) {
        AiToolHandler handler = handlers.get(toolCode);
        return handler != null && handler.readOnly();
    }

    public boolean isAllowedInPhase42(String toolCode) {
        return PHASE_42_ALLOWLIST.contains(toolCode) && isReadOnly(toolCode);
    }

    public AiToolResult dispatch(String toolCode, Map<String, Object> arguments, AiToolExecutionContext context) {
        if (!PHASE_42_ALLOWLIST.contains(toolCode)) {
            return AiToolResult.failure("AI_TOOL_NOT_ALLOWED",
                    "Tool '" + toolCode + "' is not in the Phase 42 read-only allowlist.");
        }

        AiToolHandler handler = handlers.get(toolCode);
        if (handler == null) {
            return AiToolResult.failure("AI_TOOL_HANDLER_NOT_FOUND",
                    "No live handler registered for tool: " + toolCode);
        }

        if (!handler.readOnly()) {
            return AiToolResult.failure("AI_TOOL_NOT_ALLOWED",
                    "Mutation tools are not callable from Phase 42 orchestrator.");
        }

        return handler.execute(arguments, context);
    }
}
