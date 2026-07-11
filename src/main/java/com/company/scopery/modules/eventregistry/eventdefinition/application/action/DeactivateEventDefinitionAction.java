package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeactivateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class DeactivateEventDefinitionAction {

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public DeactivateEventDefinitionAction(EventDefinitionRepository repository,
                                           EventRegistryActivityLogger activityLogger,
                                           IamSystemAuthorizationService systemAuthorizationService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public EventDefinitionDetailResponse execute(DeactivateEventDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

        EventDefinition eventDefinition = findOrThrow(command.id());

        eventDefinition.deactivate();
        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.DEACTIVATE_EVENT_DEFINITION,
                "Event definition deactivated: " + saved.code().value());

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
}
