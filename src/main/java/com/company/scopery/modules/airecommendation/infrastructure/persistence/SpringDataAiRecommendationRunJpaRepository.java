package com.company.scopery.modules.airecommendation.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiRecommendationRunJpaRepository
        extends JpaRepository<AiRecommendationRunJpaEntity, UUID> {

    Optional<AiRecommendationRunJpaEntity> findByWorkspaceIdAndProjectIdAndIdempotencyKey(
            UUID workspaceId, UUID projectId, String idempotencyKey);
}
