package com.company.scopery.modules.trust.sensitiveobject.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSensitiveObjectRegistryJpaRepository extends JpaRepository<SensitiveObjectRegistryJpaEntity, UUID> {
    List<SensitiveObjectRegistryJpaEntity> findByWorkspaceId(UUID workspaceId);
    Optional<SensitiveObjectRegistryJpaEntity> findByWorkspaceIdAndObjectTypeCode(UUID workspaceId, String objectTypeCode);
}
