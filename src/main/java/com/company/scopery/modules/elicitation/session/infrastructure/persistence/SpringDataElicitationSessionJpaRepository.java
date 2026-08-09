package com.company.scopery.modules.elicitation.session.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataElicitationSessionJpaRepository
        extends JpaRepository<ElicitationSessionJpaEntity, UUID> {

    List<ElicitationSessionJpaEntity> findByProjectId(UUID projectId);

    boolean existsByProjectIdAndScopePackageIdAndStatus(UUID projectId, UUID scopePackageId, String status);
}
