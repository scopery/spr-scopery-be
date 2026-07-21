package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionPlanJpaRepository extends JpaRepository<AiActionPlanJpaEntity, UUID> {

    @Query("SELECT p FROM AiActionPlanJpaEntity p WHERE p.requestId = :requestId ORDER BY p.planNumber DESC LIMIT 1")
    Optional<AiActionPlanJpaEntity> findLatestByRequestId(UUID requestId);
}
