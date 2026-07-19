package com.company.scopery.modules.clientportal.grant.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataExternalProjectAccessGrantJpaRepository extends JpaRepository<ExternalProjectAccessGrantJpaEntity, UUID> {
    Optional<ExternalProjectAccessGrantJpaEntity> findByIdAndProjectId(UUID id, UUID projectId);
    List<ExternalProjectAccessGrantJpaEntity> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ExternalProjectAccessGrantJpaEntity> findByProjectIdAndPortalAccountId(UUID projectId, UUID portalAccountId);
    List<ExternalProjectAccessGrantJpaEntity> findByPortalAccountIdOrderByCreatedAtDesc(UUID portalAccountId);
}
