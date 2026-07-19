package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiConversationRepository {

    AiConversation save(AiConversation conversation);

    Optional<AiConversation> findById(UUID id);

    Optional<AiConversation> findByIdAndOwnerUserId(UUID id, UUID ownerUserId);

    Page<AiConversation> findByWorkspaceIdAndOwnerUserIdAndStatus(
            UUID workspaceId, UUID ownerUserId, ConversationStatus status, Pageable pageable);

    List<AiConversation> findByStatusAndLastMessageAtBefore(ConversationStatus status, Instant threshold);

    List<AiConversation> findByStatusAndDeletedAtBefore(ConversationStatus status, Instant threshold);
}
