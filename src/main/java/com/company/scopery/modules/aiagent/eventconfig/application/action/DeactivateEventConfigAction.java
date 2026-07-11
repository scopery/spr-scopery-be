package com.company.scopery.modules.aiagent.eventconfig.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.application.command.DeactivateEventConfigCommand;
import com.company.scopery.modules.aiagent.eventconfig.application.response.EventConfigDetailResponse;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplate;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptTemplateRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivateEventConfigAction {

    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateEventConfigAction(EventConfigRepository eventConfigRepository,
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
    public EventConfigDetailResponse execute(DeactivateEventConfigCommand command) {
        EventConfig config = findOrThrow(command.id());
        config.deactivate();
        EventConfig saved = eventConfigRepository.save(config);

        activityLogger.logSuccess(AiAgentEntityTypes.EVENT_CONFIG, saved.id(),
                AiAgentActivityActions.DEACTIVATE_EVENT_CONFIG,
                "Event config deactivated: " + saved.code().value());

        return buildDetailResponse(saved);
    }

    private EventConfig findOrThrow(UUID id) {
        return eventConfigRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.eventConfigNotFound(id));
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
