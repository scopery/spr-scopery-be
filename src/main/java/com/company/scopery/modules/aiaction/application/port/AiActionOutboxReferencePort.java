package com.company.scopery.modules.aiaction.application.port;

import java.util.UUID;

public interface AiActionOutboxReferencePort {

    String publishEvent(String eventType, UUID aggregateId, String payloadJson);
}
