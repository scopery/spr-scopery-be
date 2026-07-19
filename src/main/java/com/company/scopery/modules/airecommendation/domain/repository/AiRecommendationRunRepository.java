package com.company.scopery.modules.airecommendation.domain.repository;

import com.company.scopery.modules.airecommendation.domain.model.AiRecommendationRun;

import java.util.Optional;
import java.util.UUID;

public interface AiRecommendationRunRepository {
    AiRecommendationRun save(AiRecommendationRun run);
    Optional<AiRecommendationRun> findById(UUID id);
    Optional<AiRecommendationRun> findByWorkspaceProjectAndIdempotencyKey(UUID workspaceId, UUID projectId, String idempotencyKey);
}
