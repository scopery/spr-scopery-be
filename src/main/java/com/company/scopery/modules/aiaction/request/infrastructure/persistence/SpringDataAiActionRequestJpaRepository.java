package com.company.scopery.modules.aiaction.request.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionRequestJpaRepository extends JpaRepository<AiActionRequestJpaEntity, UUID> {

    Optional<AiActionRequestJpaEntity> findByWorkspaceIdAndInitiatedByUserIdAndIdempotencyKey(
            UUID workspaceId, UUID initiatedByUserId, String idempotencyKey);

    boolean existsByWorkspaceIdAndInitiatedByUserIdAndIdempotencyKeyAndRequestHashNot(
            UUID workspaceId, UUID initiatedByUserId, String idempotencyKey, String requestHash);
}
