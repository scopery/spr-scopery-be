package com.company.scopery.modules.aiagent.agent.application.action;

import com.company.scopery.modules.aiagent.agent.application.command.DeactivateAgentCommand;
import com.company.scopery.modules.aiagent.agent.application.response.AgentDetailResponse;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivateAgentAction {

    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateAgentAction(AgentRepository agentRepository,
                                 ModelDeploymentRepository deploymentRepository,
                                 AiAgentActivityLogger activityLogger) {
        this.agentRepository = agentRepository;
        this.deploymentRepository = deploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentDetailResponse execute(DeactivateAgentCommand command) {
        Agent agent = findOrThrow(command.id());
        agent.deactivate();
        Agent saved = agentRepository.save(agent);

        activityLogger.logSuccess(AiAgentEntityTypes.AGENT, saved.id(),
                AiAgentActivityActions.DEACTIVATE_AGENT,
                "Agent deactivated: " + saved.code().value());

        String deploymentName = loadDeploymentName(saved.defaultModelDeploymentId());
        return AgentDetailResponse.from(saved, deploymentName);
    }

    private Agent findOrThrow(UUID id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.agentNotFound(id));
    }

    private String loadDeploymentName(UUID deploymentId) {
        if (deploymentId == null) return null;
        return deploymentRepository.findById(deploymentId)
                .map(ModelDeployment::name)
                .orElse(null);
    }
}
