package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataEventVariableJpaRepository
        extends JpaRepository<EventVariableJpaEntity, UUID> {

    List<EventVariableJpaEntity> findByEventDefinitionId(UUID eventDefinitionId);

    void deleteByEventDefinitionId(UUID eventDefinitionId);
}
