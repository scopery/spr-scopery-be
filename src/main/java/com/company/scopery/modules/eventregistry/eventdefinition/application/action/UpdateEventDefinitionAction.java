package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpdateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDataClassification;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryErrorCatalog;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class UpdateEventDefinitionAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final ImmutableAuditEventService auditEventService;

    public UpdateEventDefinitionAction(EventDefinitionRepository repository,
                                       EventRegistryActivityLogger activityLogger,
                                       IamSystemAuthorizationService systemAuthorizationService,
                                       ImmutableAuditEventService auditEventService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public EventDefinitionDetailResponse execute(UpdateEventDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

        EventDefinition eventDefinition = findOrThrow(command.id());
        if (eventDefinition.status() == EventDefinitionStatus.DEPRECATED) {
            throw EventRegistryExceptions.eventDefinitionDeprecatedCannotBeUpdated(eventDefinition.code().value());
        }

        if (command.inputSchema() != null && !command.inputSchema().isBlank()) {
            validateJson(command.inputSchema(), true);
        }
        if (command.outputSchema() != null && !command.outputSchema().isBlank()) {
            validateJson(command.outputSchema(), false);
        }

        boolean schemaChanged = !Objects.equals(nullToEmpty(eventDefinition.inputSchema()), nullToEmpty(command.inputSchema()))
                || !Objects.equals(nullToEmpty(eventDefinition.outputSchema()), nullToEmpty(command.outputSchema()));

        EventDataClassification classification = EventRegistryEnumParser.parseOptional(
                EventDataClassification.class,
                command.dataClassification(),
                EventRegistryErrorCatalog.INVALID_EVENT_DEFINITION_DATA_CLASSIFICATION.code(),
                "dataClassification");

        try {
            eventDefinition.update(command.name(), command.description(),
                    command.inputSchema(), command.outputSchema());
            eventDefinition.updateGovernance(classification, command.ownerModule());
        } catch (IllegalStateException ex) {
            throw EventRegistryExceptions.eventDefinitionDeprecatedCannotBeUpdated(eventDefinition.code().value());
        }

        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.UPDATE_EVENT_DEFINITION,
                "Event definition updated: " + saved.code().value());

        if (schemaChanged) {
            auditEventService.record(
                    AuditEventType.EVENT_SCHEMA_CHANGED,
                    null,
                    "USER",
                    EventRegistryEntityTypes.EVENT_DEFINITION,
                    saved.id(),
                    null,
                    null,
                    null,
                    Map.of("code", saved.code().value()),
                    "Event schema updated");
        }

        List<EventVariableResponse> variables = loadVariableResponses(saved.id());
        return EventDefinitionDetailResponse.from(saved, variables);
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

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
