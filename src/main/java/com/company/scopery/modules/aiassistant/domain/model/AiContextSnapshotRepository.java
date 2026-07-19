package com.company.scopery.modules.aiassistant.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiContextSnapshotRepository {

    AiContextSnapshot save(AiContextSnapshot snapshot);

    Optional<AiContextSnapshot> findByAssistantMessageId(UUID assistantMessageId);

    Optional<AiContextSnapshot> findByConversationIdAndTurnId(UUID conversationId, UUID turnId);

    List<AiContextSnapshot> findExpiredBefore(Instant threshold);
}
