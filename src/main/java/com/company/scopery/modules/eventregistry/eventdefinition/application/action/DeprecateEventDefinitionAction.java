package com.company.scopery.modules.eventregistry.eventdefinition.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.eventregistry.eventdefinition.application.command.DeprecateEventDefinitionCommand;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventDefinitionDetailResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.application.response.EventVariableResponse;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.shared.activity.EventRegistryActivityLogger;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryActivityActions;
import com.company.scopery.modules.eventregistry.shared.constant.EventRegistryEntityTypes;
import com.company.scopery.modules.eventregistry.shared.error.EventRegistryExceptions;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DeprecateEventDefinitionAction {

    private final EventDefinitionRepository repository;
    private final EventRegistryActivityLogger activityLogger;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final ImmutableAuditEventService auditEventService;

    public DeprecateEventDefinitionAction(EventDefinitionRepository repository,
                                          EventRegistryActivityLogger activityLogger,
                                          IamSystemAuthorizationService systemAuthorizationService,
                                          ImmutableAuditEventService auditEventService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.systemAuthorizationService = systemAuthorizationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public EventDefinitionDetailResponse execute(DeprecateEventDefinitionCommand command) {
        systemAuthorizationService.requireSystemRight(
                IamAuthorities.SYSTEM_EVENT_REGISTRY_MANAGE.legacyRightCode());

        EventDefinition eventDefinition = findOrThrow(command.id());
        if (eventDefinition.status() == EventDefinitionStatus.DEPRECATED) {
            throw EventRegistryExceptions.eventDefinitionAlreadyDeprecated(eventDefinition.code().value());
        }

        if (command.replacementEventDefinitionId() != null) {
            EventDefinition replacement = findOrThrow(command.replacementEventDefinitionId());
            if (replacement.status() != EventDefinitionStatus.ACTIVE) {
                throw EventRegistryExceptions.eventDefinitionNotFound(command.replacementEventDefinitionId());
            }
        }

        UUID actorId = currentActorId();
        Map<String, Object> before = Map.of(
                "status", eventDefinition.status().name(),
                "code", eventDefinition.code().value());

        try {
            eventDefinition.deprecate(command.replacementEventDefinitionId(), actorId);
        } catch (IllegalStateException ex) {
            throw EventRegistryExceptions.eventDefinitionAlreadyDeprecated(eventDefinition.code().value());
        }

        EventDefinition saved = repository.save(eventDefinition);

        activityLogger.logSuccess(EventRegistryEntityTypes.EVENT_DEFINITION, saved.id(),
                EventRegistryActivityActions.DEPRECATE_EVENT_DEFINITION,
                "Event definition deprecated: " + saved.code().value());

        auditEventService.record(
                AuditEventType.EVENT_DEFINITION_DEPRECATED,
                actorId,
                "USER",
                EventRegistryEntityTypes.EVENT_DEFINITION,
                saved.id(),
                null,
                null,
                before,
                Map.of(
                        "status", saved.status().name(),
                        "replacementEventDefinitionId", String.valueOf(saved.replacementEventDefinitionId()),
                        "reason", command.reason() == null ? "" : command.reason()),
                command.reason());

        return EventDefinitionDetailResponse.from(saved, loadVariableResponses(saved.id()));
    }

    private EventDefinition findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> EventRegistryExceptions.eventDefinitionNotFound(id));
    }

    private List<EventVariableResponse> loadVariableResponses(UUID eventDefinitionId) {
        return repository.findVariablesByEventDefinitionId(eventDefinitionId)
                .stream().map(EventVariableResponse::from).toList();
    }

    private static UUID currentActorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        try {
            return UUID.fromString(principal.toString());
        } catch (Exception ignored) {
            return null;
        }
    }
}
