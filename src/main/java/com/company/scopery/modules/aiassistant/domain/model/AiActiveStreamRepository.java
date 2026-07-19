package com.company.scopery.modules.aiassistant.domain.model;

import com.company.scopery.modules.aiassistant.domain.enums.ActiveStreamStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AiActiveStreamRepository {

    AiActiveStream save(AiActiveStream stream);

    Optional<AiActiveStream> findByMessageId(UUID messageId);

    int countByWorkspaceIdAndActorIdAndStreamStatus(UUID workspaceId, UUID actorId, ActiveStreamStatus status);

    List<AiActiveStream> findByStreamStatusAndExpiresAtBefore(ActiveStreamStatus status, Instant threshold);
}
