package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AiStreamEventRepository {

    AiStreamEvent save(AiStreamEvent event);

    List<AiStreamEvent> findByMessageIdAndSequenceGreaterThanOrderBySequence(UUID messageId, long afterSequence);

    int deleteByExpiresAtBefore(Instant threshold);
}
