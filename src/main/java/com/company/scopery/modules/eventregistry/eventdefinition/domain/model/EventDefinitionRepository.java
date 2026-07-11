package com.company.scopery.modules.eventregistry.eventdefinition.domain.model;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;

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
    PageResult<EventDefinition> findAll(String keyword, String sourceSystem, String eventKey,
                                         EventDefinitionStatus status, PageQuery pageQuery);

    List<EventVariable> findVariablesByEventDefinitionId(UUID eventDefinitionId);
    EventVariable saveVariable(EventVariable variable);
    void deleteVariablesByEventDefinitionId(UUID eventDefinitionId);
}
