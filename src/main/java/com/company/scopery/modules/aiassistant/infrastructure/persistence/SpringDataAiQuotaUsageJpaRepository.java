package com.company.scopery.modules.aiassistant.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

public interface SpringDataAiQuotaUsageJpaRepository
        extends JpaRepository<AiQuotaUsageJpaEntity, UUID>, JpaSpecificationExecutor<AiQuotaUsageJpaEntity> {

    Optional<AiQuotaUsageJpaEntity> findByWorkspaceIdAndActorIdAndUsageDate(
            UUID workspaceId, UUID actorId, LocalDate usageDate);
}
