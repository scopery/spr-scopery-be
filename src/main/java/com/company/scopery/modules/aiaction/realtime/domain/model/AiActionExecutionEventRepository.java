package com.company.scopery.modules.aiaction.realtime.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActionExecutionEventRepository {

    AiActionExecutionEvent save(AiActionExecutionEvent event);

    long nextSequenceForExecution(UUID executionId);

    List<AiActionExecutionEvent> findByExecutionIdAndSequenceGreaterThan(UUID executionId, long afterSequence, int limit);

    List<AiActionExecutionEvent> findUnpublishedSince(Instant since, int limit);

    Optional<Long> findMinRetainedSequence(UUID executionId);
}
