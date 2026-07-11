package com.company.scopery.modules.aiagent.agent.application.action;

import com.company.scopery.modules.aiagent.agent.application.command.UpdateAgentCommand;
import com.company.scopery.modules.aiagent.agent.application.response.AgentDetailResponse;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.enums.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateAgentAction {

    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateAgentAction(AgentRepository agentRepository,
                             ModelDeploymentRepository deploymentRepository,
                             AiAgentActivityLogger activityLogger) {
        this.agentRepository = agentRepository;
        this.deploymentRepository = deploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentDetailResponse execute(UpdateAgentCommand command) {
        Agent agent = findOrThrow(command.id());

        AgentType type = AiAgentEnumParser.parseRequired(
                AgentType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AGENT_TYPE.code(), "type");
        AgentOutputFormat outputFormat = AiAgentEnumParser.parseOptional(
                AgentOutputFormat.class, command.outputFormat(),
                AiAgentErrorCatalog.INVALID_AGENT_OUTPUT_FORMAT.code(), "outputFormat");

        boolean requireActiveDeployment = agent.status() == AgentStatus.ACTIVE;
        validateDeployment(command.defaultModelDeploymentId(), requireActiveDeployment);

        agent.update(command.name(), type, command.description(),
                command.defaultModelDeploymentId(), outputFormat);

        Agent saved = agentRepository.save(agent);

        activityLogger.logSuccess(AiAgentEntityTypes.AGENT, saved.id(),
                AiAgentActivityActions.UPDATE_AGENT,
                "Agent updated: " + saved.code().value());

        String deploymentName = loadDeploymentName(saved.defaultModelDeploymentId());
        return AgentDetailResponse.from(saved, deploymentName);
    }

    private Agent findOrThrow(UUID id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.agentNotFound(id));
    }

    private void validateDeployment(UUID deploymentId, boolean requireActive) {
        if (deploymentId == null) return;
        ModelDeployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> AiAgentExceptions.agentDefaultDeploymentNotFound(deploymentId));
        if (requireActive && deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.agentDefaultDeploymentNotActive(deployment.status().name());
        }
    }

    private String loadDeploymentName(UUID deploymentId) {
        if (deploymentId == null) return null;
        return deploymentRepository.findById(deploymentId)
                .map(ModelDeployment::name)
                .orElse(null);
    }
}
