package com.company.scopery.modules.aiaction.request.domain.model;

import java.util.Optional;
import java.util.UUID;

public interface AiActionRequestRepository {

    AiActionRequest save(AiActionRequest request);

    Optional<AiActionRequest> findById(UUID id);

    Optional<AiActionRequest> findByWorkspaceAndUserAndIdempotencyKey(UUID workspaceId, UUID userId, String idempotencyKey);

    boolean existsByWorkspaceAndUserAndIdempotencyKeyAndDifferentHash(UUID workspaceId, UUID userId, String idempotencyKey, String requestHash);
}
