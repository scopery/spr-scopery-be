package com.company.scopery.modules.aiaction.application.port;

import com.company.scopery.modules.aiaction.realtime.domain.enums.AiActionEventType;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;

import java.util.UUID;

public interface AiActionRealtimePublisherPort {

    AiActionExecutionEvent persistAndPublish(UUID executionId, int executionVersion,
                                              AiActionEventType eventType,
                                              String traceId, String payloadJson);
}
