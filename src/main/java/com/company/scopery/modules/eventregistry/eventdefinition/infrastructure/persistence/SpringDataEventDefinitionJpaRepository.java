package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEventDefinitionJpaRepository
        extends JpaRepository<EventDefinitionJpaEntity, UUID>,
                JpaSpecificationExecutor<EventDefinitionJpaEntity> {

    Optional<EventDefinitionJpaEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsBySourceSystemAndEventKey(String sourceSystem, String eventKey);

    Optional<EventDefinitionJpaEntity> findBySourceSystemAndEventKey(String sourceSystem, String eventKey);
}