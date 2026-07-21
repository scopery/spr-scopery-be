package com.company.scopery.modules.aiaction.plan.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataAiActionConfirmationJpaRepository extends JpaRepository<AiActionConfirmationJpaEntity, UUID> {

    @Query("SELECT c FROM AiActionConfirmationJpaEntity c WHERE c.planId = :planId ORDER BY c.createdAt DESC LIMIT 1")
    Optional<AiActionConfirmationJpaEntity> findLatestByPlanId(UUID planId);
}
