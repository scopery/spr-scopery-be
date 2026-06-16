package com.company.scopery.modules.eventregistry.eventdefinition.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.query.*;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.*;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.*;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistrySortFields;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EventDefinitionApplicationService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;

    public EventDefinitionApplicationService(EventDefinitionRepository repository,
                                              EventRegistryActivityLogger activityLogger) {
        this.repository = repository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EventDefinitionResponse createEventDefinition(CreateEventDefinitionCommand command) {
        EventDefinitionCode code = EventDefinitionCode.of(command.code());
        SourceSystemCode sourceSystem = SourceSystemCode.of(command.sourceSystem());
        EventKey eventKey = EventKey.of(command.eventKey());

        if (repository.existsByCode(code)) {
            throw EventRegistryExceptions.eventDefinitionCodeAlreadyExists(code.value());
        }

        if (repository.existsBySourceSystemAndEventKey(sourceSystem, eventKey)) {
            throw EventRegistryExceptions.eventDefinitionSourceEventAlreadyExists(
                    sourceSystem.value(), eventKey.value());
        }

        if (command.inputSchema() != null && !command.inputSchema().isBlank()) {
            validateJson(command.inputSchema(), true);
        }
        if (command.outputSchema() != null && !command.outputSchema().isBlank()) {
            validateJson(command.outputSchema(), false);
        }

        EventDefinition eventDefinition = EventDefinition.create(
                code, command.name(), sourceSystem, eventKey,
                command.description(), command.inputSchema(), command.outputSchema());

        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.CREATE_EVENT_DEFINITION,
                "Event definition created: " + saved.code().value());

        return EventDefinitionResponse.from(saved);
    }

    @Transactional
    public EventDefinitionDetailResponse updateEventDefinition(UpdateEventDefinitionCommand command) {
        EventDefinition eventDefinition = findOrThrow(command.id());

        if (command.inputSchema() != null && !command.inputSchema().isBlank()) {
            validateJson(command.inputSchema(), true);
        }
        if (command.outputSchema() != null && !command.outputSchema().isBlank()) {
            validateJson(command.outputSchema(), false);
        }

        eventDefinition.update(command.name(), command.description(),
                command.inputSchema(), command.outputSchema());

        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.UPDATE_EVENT_DEFINITION,
                "Event definition updated: " + saved.code().value());

        List<EventVariableResponse> variables = loadVariableResponses(saved.id());
        return EventDefinitionDetailResponse.from(saved, variables);
    }

    @Transactional(readOnly = true)
    public EventDefinitionDetailResponse getEventDefinitionDetail(GetEventDefinitionDetailQuery query) {
        EventDefinition def = findOrThrow(query.id());
        List<EventVariableResponse> variables = loadVariableResponses(def.id());
        return EventDefinitionDetailResponse.from(def, variables);
    }

    @Transactional(readOnly = true)
    public Page<EventDefinitionResponse> searchEventDefinitions(SearchEventDefinitionQuery query) {
        EventDefinitionStatus status = EventRegistryEnumParser.parseOptional(
                EventDefinitionStatus.class, query.status(),
                EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_STATUS.code(), "status");

        String normalizedSourceSystem = (query.sourceSystem() != null && !query.sourceSystem().isBlank())
                ? query.sourceSystem().trim().toUpperCase() : null;
        String normalizedEventKey = (query.eventKey() != null && !query.eventKey().isBlank())
                ? query.eventKey().trim().toUpperCase() : null;

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, EventRegistrySortFields.CREATED_AT));

        return repository
                .findAll(query.keyword(), normalizedSourceSystem, normalizedEventKey, status, pageable)
                .map(EventDefinitionResponse::from);
    }

    @Transactional
    public EventDefinitionDetailResponse activateEventDefinition(ActivateEventDefinitionCommand command) {
        EventDefinition eventDefinition = findOrThrow(command.id());

        if (eventDefinition.status() == EventDefinitionStatus.DEPRECATED) {
            throw EventRegistryExceptions.deprecatedEventDefinitionCannotBeActivated(
                    eventDefinition.code().value());
        }

        eventDefinition.activate();
        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.ACTIVATE_EVENT_DEFINITION,
                "Event definition activated: " + saved.code().value());

        List<EventVariableResponse> variables = loadVariableResponses(saved.id());
        return EventDefinitionDetailResponse.from(saved, variables);
    }

    @Transactional
    public EventDefinitionDetailResponse deactivateEventDefinition(DeactivateEventDefinitionCommand command) {
        EventDefinition eventDefinition = findOrThrow(command.id());

        eventDefinition.deactivate();
        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.DEACTIVATE_EVENT_DEFINITION,
                "Event definition deactivated: " + saved.code().value());

        List<EventVariableResponse> variables = loadVariableResponses(saved.id());
        return EventDefinitionDetailResponse.from(saved, variables);
    }

    @Transactional
    public List<EventVariableResponse> upsertEventVariables(UpsertEventVariablesCommand command) {
        EventDefinition def = findOrThrow(command.eventDefinitionId());

        repository.deleteVariablesByEventDefinitionId(def.id());

        List<EventVariable> saved = command.variables().stream()
                .map(v -> {
                    VariableType type = EventRegistryEnumParser.parseVariableType(v.variableType());
                    EventVariable variable = EventVariable.create(
                            def.id(), v.variablePath(), v.variableLabel(),
                            type, v.required(), v.description(), v.exampleValue());
                    return repository.saveVariable(variable);
                })
                .toList();

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, def.id(),
                EventRegistryActivityActions.UPSERT_EVENT_VARIABLES,
                "Event variables upserted for: " + def.code().value() + " (" + saved.size() + " variables)");

        return saved.stream().map(EventVariableResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EventVariableResponse> getEventVariables(UUID eventDefinitionId) {
        findOrThrow(eventDefinitionId);
        return loadVariableResponses(eventDefinitionId);
    }

    private EventDefinition findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(id));
    }

    private List<EventVariableResponse> loadVariableResponses(UUID eventDefinitionId) {
        return repository.findVariablesByEventDefinitionId(eventDefinitionId)
                .stream().map(EventVariableResponse::from).toList();
    }

    private void validateJson(String json, boolean isInputSchema) {
        try {
            OBJECT_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            if (isInputSchema) {
                throw EventRegistryExceptions.invalidInputSchemaJson();
            } else {
                throw EventRegistryExceptions.invalidOutputSchemaJson();
            }
        }
    }
}
