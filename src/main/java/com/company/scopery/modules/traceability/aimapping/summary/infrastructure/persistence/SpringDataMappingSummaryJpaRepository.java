package com.company.scopery.modules.traceability.aimapping.summary.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataMappingSummaryJpaRepository extends JpaRepository<MappingSummaryJpaEntity, UUID> {

    Optional<MappingSummaryJpaEntity> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    List<MappingSummaryJpaEntity> findByEntityTypeAndEntityIdIn(String entityType, List<UUID> entityIds);
}
