package com.company.scopery.modules.aiassistant.guide.application.action;

import com.company.scopery.modules.aiassistant.guide.application.command.ExplainPageCommand;
import com.company.scopery.modules.aiassistant.message.application.response.AiSseStartResponse;
import com.company.scopery.modules.aiassistant.shared.error.AiAssistantExceptions;
import org.springframework.stereotype.Component;

/**
 * Guide-flow orchestration is not yet implemented.
 * Throws a service-unavailable error so callers receive immediate feedback
 * instead of waiting for a placeholder SSE stream that never emits events.
 */
@Component
public class ExplainPageAction {

    public AiSseStartResponse execute(ExplainPageCommand cmd) {
        throw AiAssistantExceptions.guideNotAvailable();
    }
}
