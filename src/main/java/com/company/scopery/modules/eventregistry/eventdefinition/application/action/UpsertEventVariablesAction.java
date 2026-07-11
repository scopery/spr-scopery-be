package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpsertEventVariablesCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class UpsertEventVariablesAction {

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;

    public UpsertEventVariablesAction(EventDefinitionRepository repository,
                                      EventRegistryActivityLogger activityLogger,
                                      IamSystemAuthorizationService systemAuthorizationService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
    }

    @Transactional
    public List<EventVariableResponse> execute(UpsertEventVariablesCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

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

    private EventDefinition findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(id));
    }
}
