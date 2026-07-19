package com.company.scopery.modules.integrationhub.deadletter.domain.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeadLetterEventRepository {
    DeadLetterEvent save(DeadLetterEvent event);
    Optional<DeadLetterEvent> findById(UUID id);
    List<DeadLetterEvent> findByWorkspaceId(UUID workspaceId);
}
