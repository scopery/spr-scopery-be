package com.company.scopery.modules.aiagent.eventconfig.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.eventconfig.application.command.ActivateEventConfigCommand;
import com.company.scopery.modules.aiagent.eventconfig.application.response.EventConfigDetailResponse;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigEnvironment;
import com.company.scopery.modules.aiagent.eventconfig.domain.enums.EventConfigStatus;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptTemplateStatus;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.prompt.domain.enums.PromptVersionStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.enums.EventDefinitionStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateEventConfigAction {

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateEventConfigAction(EventConfigRepository eventConfigRepository,
                                     EventDefinitionRepository eventDefinitionRepository,
                                     AgentRepository agentRepository,
                                     PromptVersionRepository promptVersionRepository,
                                     PromptTemplateRepository promptTemplateRepository,
                                     ModelDeploymentRepository modelDeploymentRepository,
                                     AiAgentActivityLogger activityLogger) {
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public EventConfigDetailResponse execute(ActivateEventConfigCommand command) {
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

    private EventConfig findOrThrow(UUID id) {
        return eventConfigRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.eventConfigNotFound(id));
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

    private EventConfigDetailResponse buildDetailResponse(EventConfig config) {
        EventDefinition eventDef = eventDefinitionRepository.findById(config.eventDefinitionId()).orElse(null);
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
