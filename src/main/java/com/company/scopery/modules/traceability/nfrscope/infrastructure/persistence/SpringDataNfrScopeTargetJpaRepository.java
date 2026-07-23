package com.company.scopery.modules.traceability.nfrscope.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataNfrScopeTargetJpaRepository
        extends JpaRepository<NfrScopeTargetJpaEntity, NfrScopeTargetId> {

    boolean existsByIdNfrIdAndIdTargetId(UUID nfrId, UUID targetId);

    Optional<NfrScopeTargetJpaEntity> findByIdNfrIdAndIdTargetId(UUID nfrId, UUID targetId);

    List<NfrScopeTargetJpaEntity> findByIdNfrId(UUID nfrId);

    List<NfrScopeTargetJpaEntity> findByIdTargetId(UUID targetId);

    void deleteByIdNfrIdAndIdTargetId(UUID nfrId, UUID targetId);
}
