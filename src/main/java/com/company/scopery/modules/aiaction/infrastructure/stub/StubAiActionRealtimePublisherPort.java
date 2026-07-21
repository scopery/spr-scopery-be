package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionRealtimePublisherPort;
import com.company.scopery.modules.aiaction.realtime.domain.enums.AiActionEventType;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEventRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub implementation — replaced in Step 17 (persists event, skips Redis publish)
@Component
public class StubAiActionRealtimePublisherPort implements AiActionRealtimePublisherPort {

    private final AiActionExecutionEventRepository eventRepository;

    public StubAiActionRealtimePublisherPort(AiActionExecutionEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public AiActionExecutionEvent persistAndPublish(UUID executionId, int executionVersion,
                                                     AiActionEventType eventType,
                                                     String traceId, String payloadJson) {
        long sequence = eventRepository.nextSequenceForExecution(executionId);
        AiActionExecutionEvent event = AiActionExecutionEvent.create(
                executionId, sequence, executionVersion, eventType, traceId, payloadJson);
        return eventRepository.save(event);
    }
}
