package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.modules.eventregistry.eventdefinition.application.command.UpsertEventVariablesCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.service.EventDefinitionConsumerSafetyService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.VariableType;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventVariable;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.valueobject.EventVariablePath;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.eventregistry.shared.util.EventRegistryEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UpsertEventVariablesAction {

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final EventDefinitionConsumerSafetyService consumerSafetyService;

    public UpsertEventVariablesAction(EventDefinitionRepository repository,
                                      EventRegistryActivityLogger activityLogger,
                                      IamSystemAuthorizationService systemAuthorizationService,
                                      EventDefinitionConsumerSafetyService consumerSafetyService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
        this.consumerSafetyService = consumerSafetyService;
    }

    @Transactional
    public List<EventVariableResponse> execute(UpsertEventVariablesCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

        EventDefinition def = findOrThrow(command.eventDefinitionId());
        if (def.status() == EventDefinitionStatus.DEPRECATED) {
            throw EventRegistryExceptions.eventDefinitionDeprecatedCannotBeUpdated(def.code().value());
        }

        List<EventVariable> existing = repository.findVariablesByEventDefinitionId(def.id());
        Map<String, EventVariable> existingByPath = existing.stream()
                .collect(Collectors.toMap(EventVariable::variablePath, Function.identity(), (a, b) -> a));

        Set<String> seenPaths = new HashSet<>();
        List<ParsedVariable> parsed = command.variables().stream()
                .map(v -> parseEntry(v, seenPaths))
                .toList();

        boolean hasActiveConsumers = consumerSafetyService.hasActiveConsumers(def.id());
        if (hasActiveConsumers) {
            assertConsumerSafeUpsert(existingByPath, parsed);
        }

        repository.deleteVariablesByEventDefinitionId(def.id());

        List<EventVariable> saved = parsed.stream()
                .map(p -> repository.saveVariable(EventVariable.create(
                        def.id(), p.path(), p.label(), p.type(), p.required(), p.sensitive(),
                        p.description(), p.exampleValue())))
                .toList();

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, def.id(),
                EventRegistryActivityActions.UPSERT_EVENT_VARIABLES,
                "Event variables upserted for: " + def.code().value() + " (" + saved.size() + " variables)");

        return saved.stream().map(EventVariableResponse::from).toList();
    }

    private ParsedVariable parseEntry(UpsertEventVariablesCommand.VariableEntry entry, Set<String> seenPaths) {
        String path;
        try {
            path = EventVariablePath.of(entry.variablePath()).value();
        } catch (IllegalArgumentException ex) {
            throw EventRegistryExceptions.eventVariableInvalidPath(entry.variablePath());
        }
        if (!seenPaths.add(path)) {
            throw EventRegistryExceptions.eventVariableDuplicatePath(path);
        }
        VariableType type = EventRegistryEnumParser.parseVariableType(entry.variableType());
        return new ParsedVariable(path, entry.variableLabel(), type, entry.required(),
                entry.sensitive(), entry.description(), entry.exampleValue());
    }

    private void assertConsumerSafeUpsert(Map<String, EventVariable> existingByPath,
                                          List<ParsedVariable> incoming) {
        Map<String, ParsedVariable> incomingByPath = incoming.stream()
                .collect(Collectors.toMap(ParsedVariable::path, Function.identity(), (a, b) -> a));

        for (EventVariable existing : existingByPath.values()) {
            ParsedVariable next = incomingByPath.get(existing.variablePath());
            if (next == null) {
                if (existing.required()) {
                    throw EventRegistryExceptions.eventVariableRequiredRemovalBlocked(existing.variablePath());
                }
                continue;
            }
            if (existing.variableType() != next.type()) {
                throw EventRegistryExceptions.eventVariableTypeChangeBlocked(existing.variablePath());
            }
        }
    }

    private EventDefinition findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(id));
    }

    private record ParsedVariable(
            String path,
            String label,
            VariableType type,
            boolean required,
            boolean sensitive,
            String description,
            String exampleValue
    ) {}
}
