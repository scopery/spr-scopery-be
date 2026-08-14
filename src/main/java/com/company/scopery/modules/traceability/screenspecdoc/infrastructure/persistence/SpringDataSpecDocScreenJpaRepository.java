package com.company.scopery.modules.traceability.screenspecdoc.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataSpecDocScreenJpaRepository
        extends JpaRepository<SpecDocScreenJpaEntity, SpecDocScreenId> {

    List<SpecDocScreenJpaEntity> findByIdDocumentId(UUID documentId);

    Optional<SpecDocScreenJpaEntity> findByIdDocumentIdAndIdScreenId(UUID documentId, UUID screenId);

    boolean existsByIdDocumentIdAndIdScreenId(UUID documentId, UUID screenId);

    void deleteByIdDocumentIdAndIdScreenId(UUID documentId, UUID screenId);
}
