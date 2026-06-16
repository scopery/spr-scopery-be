package com.company.scopery.modules.aiagent.eventconfig.application;

import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.AgentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.application.command.*;
import com.company.scopery.modules.aiagent.eventconfig.application.query.*;
import com.company.scopery.modules.aiagent.eventconfig.application.response.*;
import com.company.scopery.modules.aiagent.eventconfig.domain.*;
import com.company.scopery.modules.aiagent.prompt.domain.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionStatus;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventKey;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.SourceSystemCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EventConfigApplicationService {

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final String runtimeEnvironment;

    public EventConfigApplicationService(EventConfigRepository eventConfigRepository,
                                         EventDefinitionRepository eventDefinitionRepository,
                                         AgentRepository agentRepository,
                                         PromptVersionRepository promptVersionRepository,
                                         PromptTemplateRepository promptTemplateRepository,
                                         ModelDeploymentRepository modelDeploymentRepository,
                                         AiAgentActivityLogger activityLogger,
                                         @Value("${scopery.aiagent.runtime-environment}")
                                         String runtimeEnvironment) {
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
        this.runtimeEnvironment = runtimeEnvironment;
    }

    @Transactional
    public EventConfigResponse createEventConfig(CreateEventConfigCommand command) {
        EventConfigCode code = EventConfigCode.of(command.code());
        if (eventConfigRepository.existsByCode(code)) {
            throw AiAgentExceptions.eventConfigCodeAlreadyExists(code.value());
        }

        EventConfigEnvironment environment = parseEnvironment(command.environment());

        EventTriggerType triggerType = AiAgentEnumParser.parseRequired(
                EventTriggerType.class, command.triggerType(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_TRIGGER_TYPE.code(), "triggerType");

        if (eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(
                command.eventDefinitionId(), environment, null)) {
            throw AiAgentExceptions.eventConfigActiveAlreadyExists(command.eventDefinitionId(), environment.name());
        }

        validateDependencies(command.eventDefinitionId(), command.agentId(),
                command.promptVersionId(), command.modelDeploymentId(), environment);

        EventConfig config = EventConfig.create(code, command.name(), command.eventDefinitionId(),
                environment, triggerType, command.agentId(), command.promptVersionId(),
                command.modelDeploymentId(), command.conditionExpression(), command.description());

        EventConfig saved = eventConfigRepository.save(config);

        activityLogger.logSuccess(AiAgentEntityTypes.EVENT_CONFIG, saved.id(),
                AiAgentActivityActions.CREATE_EVENT_CONFIG,
                "Event config created: " + saved.code().value()
                        + " for eventDefinition=" + saved.eventDefinitionId()
                        + " env=" + saved.environment().name());

        return EventConfigResponse.from(saved);
    }

    @Transactional
    public EventConfigDetailResponse updateEventConfig(UpdateEventConfigCommand command) {
        EventConfig config = findOrThrow(command.id());

        EventTriggerType triggerType = AiAgentEnumParser.parseRequired(
                EventTriggerType.class, command.triggerType(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_TRIGGER_TYPE.code(), "triggerType");

        if (config.status() == EventConfigStatus.ACTIVE) {
            validateDependencies(config.eventDefinitionId(), command.agentId(),
                    command.promptVersionId(), command.modelDeploymentId(), config.environment());
        }

        config.update(command.name(), triggerType, command.agentId(), command.promptVersionId(),
                command.modelDeploymentId(), command.conditionExpression(), command.description());

        EventConfig saved = eventConfigRepository.save(config);

        activityLogger.logSuccess(AiAgentEntityTypes.EVENT_CONFIG, saved.id(),
                AiAgentActivityActions.UPDATE_EVENT_CONFIG,
                "Event config updated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public EventConfigDetailResponse getEventConfigDetail(GetEventConfigDetailQuery query) {
        EventConfig config = findOrThrow(query.id());
        return buildDetailResponse(config);
    }

    @Transactional(readOnly = true)
    public Page<EventConfigResponse> searchEventConfigs(SearchEventConfigQuery query) {
        EventConfigEnvironment environment = AiAgentEnumParser.parseOptional(
                EventConfigEnvironment.class, query.environment(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");
        EventTriggerType triggerType = AiAgentEnumParser.parseOptional(
                EventTriggerType.class, query.triggerType(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_TRIGGER_TYPE.code(), "triggerType");
        EventConfigStatus status = AiAgentEnumParser.parseOptional(
                EventConfigStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_STATUS.code(), "status");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return eventConfigRepository
                .findAll(query.keyword(), query.eventDefinitionId(), environment,
                        triggerType, status, query.agentId(), pageable)
                .map(EventConfigResponse::from);
    }

    @Transactional
    public EventConfigDetailResponse activateEventConfig(ActivateEventConfigCommand command) {
        EventConfig config = findOrThrow(command.id());

        if (config.status() == EventConfigStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedEventConfigCannotBeActivated(config.code().value());
        }

        if (eventConfigRepository.existsActiveByEventDefinitionIdAndEnvironment(
                config.eventDefinitionId(), config.environment(), config.id())) {
            throw AiAgentExceptions.eventConfigActiveAlreadyExists(
                    config.eventDefinitionId(), config.environment().name());
        }

        validateDependencies(config.eventDefinitionId(), config.agentId(),
                config.promptVersionId(), config.modelDeploymentId(), config.environment());

        config.activate();
        EventConfig saved = eventConfigRepository.save(config);

        activityLogger.logSuccess(AiAgentEntityTypes.EVENT_CONFIG, saved.id(),
                AiAgentActivityActions.ACTIVATE_EVENT_CONFIG,
                "Event config activated: " + saved.code().value()
                        + " env=" + saved.environment().name());

        return buildDetailResponse(saved);
    }

    @Transactional
    public EventConfigDetailResponse deactivateEventConfig(DeactivateEventConfigCommand command) {
        EventConfig config = findOrThrow(command.id());
        config.deactivate();
        EventConfig saved = eventConfigRepository.save(config);

        activityLogger.logSuccess(AiAgentEntityTypes.EVENT_CONFIG, saved.id(),
                AiAgentActivityActions.DEACTIVATE_EVENT_CONFIG,
                "Event config deactivated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public EventConfigDetailResponse resolveActiveEventConfig(ResolveActiveEventConfigQuery query) {
        UUID eventDefinitionId = resolveEventDefinitionId(query);

        EventConfigEnvironment environment = parseEnvironment(query.environment());

        EventConfig config = eventConfigRepository
                .findActiveByEventDefinitionIdAndEnvironment(eventDefinitionId, environment)
                .orElseThrow(() -> AiAgentExceptions.activeEventConfigNotFound(
                        eventDefinitionId, environment.name()));

        return buildDetailResponse(config);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private UUID resolveEventDefinitionId(ResolveActiveEventConfigQuery query) {
        if (query.eventDefinitionId() != null) {
            return query.eventDefinitionId();
        }
        String sourceSystem = query.sourceSystem();
        String eventKey = query.eventKey();
        if (sourceSystem == null || sourceSystem.isBlank() || eventKey == null || eventKey.isBlank()) {
            throw new ValidationException(
                    "Either eventDefinitionId or both sourceSystem and eventKey are required");
        }
        SourceSystemCode sc = SourceSystemCode.of(sourceSystem);
        EventKey ek = EventKey.of(eventKey);
        EventDefinition ed = eventDefinitionRepository.findBySourceSystemAndEventKey(sc, ek)
                .orElseThrow(() -> AiAgentExceptions.eventConfigEventDefinitionNotFound(null));
        return ed.id();
    }

    private void validateDependencies(UUID eventDefinitionId, UUID agentId,
                                       UUID promptVersionId, UUID modelDeploymentId,
                                       EventConfigEnvironment environment) {
        EventDefinition eventDef = eventDefinitionRepository.findById(eventDefinitionId)
                .orElseThrow(() -> AiAgentExceptions.eventConfigEventDefinitionNotFound(eventDefinitionId));
        if (eventDef.status() != EventDefinitionStatus.ACTIVE) {
            throw AiAgentExceptions.eventConfigEventDefinitionNotActive(eventDefinitionId);
        }

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> AiAgentExceptions.eventConfigAgentNotFound(agentId));
        if (agent.status() != AgentStatus.ACTIVE) {
            throw AiAgentExceptions.eventConfigAgentNotActive(agentId);
        }

        PromptVersion promptVersion = promptVersionRepository.findById(promptVersionId)
                .orElseThrow(() -> AiAgentExceptions.eventConfigPromptVersionNotFound(promptVersionId));
        if (promptVersion.status() != PromptVersionStatus.ACTIVE) {
            throw AiAgentExceptions.eventConfigPromptVersionNotActive(promptVersionId);
        }

        PromptTemplate promptTemplate = promptTemplateRepository.findById(promptVersion.templateId())
                .orElseThrow(() -> AiAgentExceptions.eventConfigPromptTemplateNotFound(promptVersion.templateId()));
        if (promptTemplate.status() != PromptTemplateStatus.ACTIVE) {
            throw AiAgentExceptions.eventConfigPromptTemplateNotActive(promptTemplate.id());
        }
        if (!promptTemplate.agentId().equals(agentId)) {
            throw AiAgentExceptions.eventConfigPromptTemplateAgentMismatch(promptTemplate.id(), agentId);
        }

        ModelDeployment deployment = modelDeploymentRepository.findById(modelDeploymentId)
                .orElseThrow(() -> AiAgentExceptions.eventConfigModelDeploymentNotFound(modelDeploymentId));
        if (deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.eventConfigModelDeploymentNotActive(modelDeploymentId);
        }
        if (!deployment.environment().name().equals(environment.name())) {
            throw AiAgentExceptions.eventConfigModelDeploymentEnvironmentMismatch(
                    deployment.environment().name(), environment.name());
        }
    }

    private EventConfig findOrThrow(UUID id) {
        return eventConfigRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.eventConfigNotFound(id));
    }

    private EventConfigEnvironment parseEnvironment(String raw) {
        String resolved = (raw == null || raw.isBlank()) ? runtimeEnvironment : raw;
        return AiAgentEnumParser.parseRequired(
                EventConfigEnvironment.class, resolved,
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");
    }

    private EventConfigDetailResponse buildDetailResponse(EventConfig config) {
        EventDefinition eventDef = eventDefinitionRepository.findById(config.eventDefinitionId())
                .orElse(null);
        String eventDefinitionCode = eventDef != null ? eventDef.code().value() : null;
        String sourceSystem = eventDef != null ? eventDef.sourceSystem().value() : null;
        String eventKey = eventDef != null ? eventDef.eventKey().value() : null;

        Agent agent = agentRepository.findById(config.agentId()).orElse(null);
        String agentName = agent != null ? agent.name() : null;

        PromptVersion promptVersion = promptVersionRepository.findById(config.promptVersionId()).orElse(null);
        Integer promptVersionNumber = promptVersion != null ? promptVersion.versionNumber() : null;
        String promptTemplateCode = null;
        if (promptVersion != null) {
            PromptTemplate template = promptTemplateRepository.findById(promptVersion.templateId()).orElse(null);
            promptTemplateCode = template != null ? template.code().value() : null;
        }

        ModelDeployment deployment = modelDeploymentRepository.findById(config.modelDeploymentId()).orElse(null);
        String deploymentCode = deployment != null ? deployment.code().value() : null;
        String deploymentName = deployment != null ? deployment.name() : null;

        return EventConfigDetailResponse.from(config, eventDefinitionCode, sourceSystem, eventKey,
                agentName, promptTemplateCode, promptVersionNumber, deploymentCode, deploymentName);
    }
}