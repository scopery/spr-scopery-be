package com.company.scopery.modules.trust.sensitivefield.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSensitiveFieldRegistryJpaRepository extends JpaRepository<SensitiveFieldRegistryJpaEntity, UUID> {
    List<SensitiveFieldRegistryJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<SensitiveFieldRegistryJpaEntity> findByWorkspaceIdAndObjectTypeCodeAndFieldPath(
            UUID workspaceId, String objectTypeCode, String fieldPath);
}
