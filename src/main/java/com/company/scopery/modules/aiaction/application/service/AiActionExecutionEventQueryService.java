package com.company.scopery.modules.aiaction.application.service;

import com.company.scopery.modules.aiaction.application.response.AiActionExecutionEventResponse;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEvent;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionExecutionEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiActionExecutionEventQueryService {

    private final AiActionExecutionEventRepository eventRepository;

    public AiActionExecutionEventQueryService(AiActionExecutionEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<AiActionExecutionEventResponse> getEvents(UUID executionId, long afterSequence, int limit) {
        return eventRepository.findByExecutionIdAndSequenceGreaterThan(executionId, afterSequence, limit)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AiActionExecutionEventResponse toResponse(AiActionExecutionEvent event) {
        return new AiActionExecutionEventResponse(
                event.sequence(), event.executionVersion(),
                event.eventType().name(), event.occurredAt(), event.payloadJson());
    }
}
