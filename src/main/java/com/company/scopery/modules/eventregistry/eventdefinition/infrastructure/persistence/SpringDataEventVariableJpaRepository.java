package com.company.scopery.modules.eventregistry.eventdefinition.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataEventVariableJpaRepository
        extends JpaRepository<EventVariableJpaEntity, UUID> {

    List<EventVariableJpaEntity> findByEventDefinitionId(UUID eventDefinitionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM EventVariableJpaEntity v WHERE v.eventDefinitionId = :eventDefinitionId")
    int deleteByEventDefinitionIdBulk(@Param("eventDefinitionId") UUID eventDefinitionId);
}
