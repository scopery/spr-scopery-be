package com.company.scopery.modules.eventregistry.eventdefinition.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.GetEventDefinitionDetailQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.SearchEventDefinitionQuery;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistrySortFields;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventDefinitionQueryService {

    private final EventDefinitionRepository repository;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public EventDefinitionQueryService(EventDefinitionRepository repository,
                                       IamSystemAuthorizationService systemAuthorizationService) {
        this.repository = repository;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional(readOnly = true)
    public EventDefinitionDetailResponse getEventDefinitionDetail(GetEventDefinitionDetailQuery query) {
        requireView();
        EventDefinition def = repository.findById(query.id())
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(query.id()));
        List<EventVariableResponse> variables = loadVariableResponses(def.id());
        return EventDefinitionDetailResponse.from(def, variables);
    }

    @Transactional(readOnly = true)
    public PageResult<EventDefinitionResponse> searchEventDefinitions(SearchEventDefinitionQuery query) {
        requireView();
        EventDefinitionStatus status = EventRegistryEnumParser.parseOptional(
                EventDefinitionStatus.class, query.status(),
                EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_STATUS.code(), "status");

        String normalizedSourceSystem = (query.sourceSystem() != null && !query.sourceSystem().isBlank())
                ? query.sourceSystem().trim().toUpperCase() : null;
        String normalizedEventKey = normalizeEventKeyFilter(query.eventKey());

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), EventRegistrySortFields.CREATED_AT, false);

        return repository
                .findAll(query.keyword(), normalizedSourceSystem, normalizedEventKey, status, pageQuery)
                .map(EventDefinitionResponse::from);
    }

    @Transactional(readOnly = true)
    public List<EventVariableResponse> getEventVariables(UUID eventDefinitionId) {
        requireView();
        repository.findById(eventDefinitionId)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(eventDefinitionId));
        return loadVariableResponses(eventDefinitionId);
    }

    private void requireView() {
        // Phase 05: EVENT_REGISTRY_VIEW maps to SYSTEM_MANAGE_EVENT_REGISTRY until granular split.
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());
    }

    private static String normalizeEventKeyFilter(String eventKey) {
        if (eventKey == null || eventKey.isBlank()) {
            return null;
        }
        String trimmed = eventKey.trim();
        if (trimmed.contains(".")) {
            return trimmed.toLowerCase();
        }
        return trimmed.toUpperCase();
    }

    private List<EventVariableResponse> loadVariableResponses(UUID eventDefinitionId) {
        return repository.findVariablesByEventDefinitionId(eventDefinitionId)
                .stream().map(EventVariableResponse::from).toList();
    }
}
