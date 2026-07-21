package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionPreviewJpaRepository extends JpaRepository<AiActionPreviewJpaEntity, UUID> {

    Optional<AiActionPreviewJpaEntity> findByPlanId(UUID planId);
}
