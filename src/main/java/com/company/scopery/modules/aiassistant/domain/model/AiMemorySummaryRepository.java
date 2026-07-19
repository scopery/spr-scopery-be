package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.MemorySummaryStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiMemorySummaryRepository {

    AiMemorySummary save(AiMemorySummary summary);

    Optional<AiMemorySummary> findById(UUID id);

    Optional<AiMemorySummary> findByConversationIdAndStatus(UUID conversationId, MemorySummaryStatus status);

    List<AiMemorySummary> findByConversationIdOrderByVersionDesc(UUID conversationId);

    int countByConversationId(UUID conversationId);
}
