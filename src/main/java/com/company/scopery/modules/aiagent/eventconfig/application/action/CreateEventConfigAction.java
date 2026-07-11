package com.company.scopery.modules.aiagent.eventconfig.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.application.command.CreateEventConfigCommand;
import com.company.scopery.modules.aiagent.eventconfig.application.response.EventConfigResponse;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventTriggerType;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.valueobject.EventConfigCode;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateEventConfigAction {

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final String runtimeEnvironment;

    public CreateEventConfigAction(EventConfigRepository eventConfigRepository,
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
    public EventConfigResponse execute(CreateEventConfigCommand command) {
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

    private EventConfigEnvironment parseEnvironment(String raw) {
        String resolved = (raw == null || raw.isBlank()) ? runtimeEnvironment : raw;
        return AiAgentEnumParser.parseRequired(
                EventConfigEnvironment.class, resolved,
                AiAgentErrorCatalog.INVALID_EVENT_CONFIG_ENVIRONMENT.code(), "environment");
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
}
