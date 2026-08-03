package com.company.scopery.modules.traceability.aimapping.run.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataMappingRunJpaRepository extends JpaRepository<MappingRunJpaEntity, UUID> {

    boolean existsByProjectIdAndRelationTypeAndStatus(UUID projectId, String relationType, String status);
}
