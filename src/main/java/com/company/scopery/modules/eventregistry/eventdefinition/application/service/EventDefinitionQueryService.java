package com.company.scopery.modules.eventregistry.eventdefinition.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.GetEventDefinitionDetailQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.SearchEventDefinitionQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistrySortFields;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventDefinitionQueryService {

    private final EventDefinitionRepository repository;

    public EventDefinitionQueryService(EventDefinitionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public EventDefinitionDetailResponse getEventDefinitionDetail(GetEventDefinitionDetailQuery query) {
        EventDefinition def = repository.findById(query.id())
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(query.id()));
        List<EventVariableResponse> variables = loadVariableResponses(def.id());
        return EventDefinitionDetailResponse.from(def, variables);
    }

    @Transactional(readOnly = true)
    public PageResult<EventDefinitionResponse> searchEventDefinitions(SearchEventDefinitionQuery query) {
        EventDefinitionStatus status = EventRegistryEnumParser.parseOptional(
                EventDefinitionStatus.class, query.status(),
                EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_STATUS.code(), "status");

        String normalizedSourceSystem = (query.sourceSystem() != null && !query.sourceSystem().isBlank())
                ? query.sourceSystem().trim().toUpperCase() : null;
        String normalizedEventKey = (query.eventKey() != null && !query.eventKey().isBlank())
                ? query.eventKey().trim().toUpperCase() : null;

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), EventRegistrySortFields.CREATED_AT, false);

        return repository
                .findAll(query.keyword(), normalizedSourceSystem, normalizedEventKey, status, pageQuery)
                .map(EventDefinitionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<EventVariableResponse> getEventVariables(UUID eventDefinitionId) {
        repository.findById(eventDefinitionId)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(eventDefinitionId));
        return loadVariableResponses(eventDefinitionId);
    }

    private List<EventVariableResponse> loadVariableResponses(UUID eventDefinitionId) {
        return repository.findVariablesByEventDefinitionId(eventDefinitionId)
                .stream().map(EventVariableResponse::from).toList();
    }
}
