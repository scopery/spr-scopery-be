package com.company.scopery.modules.aiaction.infrastructure.stub;

import com.company.scopery.modules.aiaction.application.port.AiActionOutboxReferencePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

// Stub implementation — replaced by real outbox publisher
@Component
public class StubAiActionOutboxReferencePort implements AiActionOutboxReferencePort {

    @Override
    public String publishEvent(String eventType, UUID aggregateId, String payloadJson) {
        return "stub-outbox-ref-" + UUID.randomUUID();
    }
}
