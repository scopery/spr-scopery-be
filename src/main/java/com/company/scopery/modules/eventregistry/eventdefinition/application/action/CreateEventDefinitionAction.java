package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.CreateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventDefinitionCode;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.SourceSystemCode;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateEventDefinitionAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public CreateEventDefinitionAction(EventDefinitionRepository repository,
                                       EventRegistryActivityLogger activityLogger,
                                       IamSystemAuthorizationService systemAuthorizationService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public EventDefinitionResponse execute(CreateEventDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

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
