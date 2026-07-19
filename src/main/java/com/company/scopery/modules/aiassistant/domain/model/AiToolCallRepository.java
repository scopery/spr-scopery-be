package com.company.scopery.modules.aiassistant.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiToolCallRepository {

    AiToolCallRecord save(AiToolCallRecord record);

    Optional<AiToolCallRecord> findById(UUID id);

    Optional<AiToolCallRecord> findByRequestMessageId(UUID requestMessageId);

    List<AiToolCallRecord> findByConversationIdAndTurnId(UUID conversationId, UUID turnId);
}
