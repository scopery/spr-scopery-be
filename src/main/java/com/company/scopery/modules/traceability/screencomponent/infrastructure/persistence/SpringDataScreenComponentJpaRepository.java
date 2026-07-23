package com.company.scopery.modules.traceability.screencomponent.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataScreenComponentJpaRepository
        extends JpaRepository<ScreenComponentJpaEntity, ScreenComponentId> {

    boolean existsByIdScreenIdAndIdComponentId(UUID screenId, UUID componentId);

    Optional<ScreenComponentJpaEntity> findByIdScreenIdAndIdComponentId(UUID screenId, UUID componentId);

    List<ScreenComponentJpaEntity> findByIdScreenIdOrderByDisplayOrderAsc(UUID screenId);

    List<ScreenComponentJpaEntity> findByIdScreenIdIn(Collection<UUID> screenIds);

    List<ScreenComponentJpaEntity> findByIdComponentId(UUID componentId);

    void deleteByIdScreenIdAndIdComponentId(UUID screenId, UUID componentId);
}
