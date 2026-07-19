package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.MessageRole;
import com.company.scopery.modules.aiassistant.domain.enums.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiMessageRepository {

    AiMessage save(AiMessage message);

    Optional<AiMessage> findById(UUID id);

    Optional<AiMessage> findByIdAndConversationId(UUID id, UUID conversationId);

    Optional<AiMessage> findByConversationIdAndIdempotencyKey(UUID conversationId, String idempotencyKey);

    int countByConversationId(UUID conversationId);

    Page<AiMessage> findByConversationIdAndRoleNotIn(UUID conversationId, List<MessageRole> excludedRoles, Pageable pageable);

    List<AiMessage> findByConversationIdAndRoleInOrderBySequenceDesc(UUID conversationId, List<MessageRole> roles, int limit);

    List<AiMessage> findByStatusInAndCreatedAtBefore(List<MessageStatus> statuses, Instant threshold);
}
