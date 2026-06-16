package com.company.scopery.modules.eventregistry.eventdefinition.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventDefinitionRepository {
    EventDefinition save(EventDefinition eventDefinition);
    Optional<EventDefinition> findById(UUID id);
    Optional<EventDefinition> findByCode(EventDefinitionCode code);
    boolean existsByCode(EventDefinitionCode code);
    boolean existsBySourceSystemAndEventKey(SourceSystemCode sourceSystem, EventKey eventKey);
    Optional<EventDefinition> findBySourceSystemAndEventKey(SourceSystemCode sourceSystem, EventKey eventKey);
    Page<EventDefinition> findAll(String keyword, String sourceSystem, String eventKey,
                                   EventDefinitionStatus status, Pageable pageable);

    List<EventVariable> findVariablesByEventDefinitionId(UUID eventDefinitionId);
    EventVariable saveVariable(EventVariable variable);
    void deleteVariablesByEventDefinitionId(UUID eventDefinitionId);
}
