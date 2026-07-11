package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpdateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class UpdateEventDefinitionAction {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public UpdateEventDefinitionAction(EventDefinitionRepository repository,
                                       EventRegistryActivityLogger activityLogger,
                                       IamSystemAuthorizationService systemAuthorizationService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public EventDefinitionDetailResponse execute(UpdateEventDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

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
