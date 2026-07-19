package com.company.scopery.modules.aiagent.agent.application.action;

import com.company.scopery.modules.aiagent.agent.application.command.CreateAgentCommand;
import com.company.scopery.modules.aiagent.agent.application.response.AgentResponse;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentAutonomyLevel;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentScope;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.agent.domain.valueobject.AgentCode;
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
public class CreateAgentAction {

    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateAgentAction(AgentRepository agentRepository,
                             ModelDeploymentRepository deploymentRepository,
                             AiAgentActivityLogger activityLogger) {
        this.agentRepository = agentRepository;
        this.deploymentRepository = deploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentResponse execute(CreateAgentCommand command) {
        AgentCode code = AgentCode.of(command.code());

        if (agentRepository.existsByCode(code)) {
            throw AiAgentExceptions.agentCodeAlreadyExists(code.value());
        }

        AgentType type = AiAgentEnumParser.parseRequired(
                AgentType.class, command.type(),
                AiAgentErrorCatalog.INVALID_AGENT_TYPE.code(), "type");
        AgentOutputFormat outputFormat = AiAgentEnumParser.parseOptional(
                AgentOutputFormat.class, command.outputFormat(),
                AiAgentErrorCatalog.INVALID_AGENT_OUTPUT_FORMAT.code(), "outputFormat");
        AgentAutonomyLevel autonomyLevel = AiAgentEnumParser.parseOptional(
                AgentAutonomyLevel.class, command.autonomyLevel(),
                AiAgentErrorCatalog.INVALID_AGENT_AUTONOMY_LEVEL.code(), "autonomyLevel");
        AgentScope scope = AiAgentEnumParser.parseOptional(
                AgentScope.class, command.scope(),
                AiAgentErrorCatalog.INVALID_AGENT_SCOPE.code(), "scope");

        rejectMutationAutonomy(autonomyLevel);
        validateDeployment(command.defaultModelDeploymentId(), true);

        Agent agent = Agent.create(command.name(), code, type, command.description(),
                command.defaultModelDeploymentId(), outputFormat,
                autonomyLevel, scope, command.organizationId(), command.workspaceId());

        Agent saved = agentRepository.save(agent);

        activityLogger.logSuccess(AiAgentEntityTypes.AGENT, saved.id(),
                AiAgentActivityActions.CREATE_AGENT,
                "Agent created: " + saved.code().value());

        return AgentResponse.from(saved);
    }

    /**
     * Phase 07: agents must not mutate business data. AUTO_EXECUTE_RESTRICTED is reserved for later phases.
     */
    static void rejectMutationAutonomy(AgentAutonomyLevel autonomyLevel) {
        if (autonomyLevel == AgentAutonomyLevel.AUTO_EXECUTE_RESTRICTED) {
            throw AiAgentExceptions.agentAutonomyNotAllowed(autonomyLevel.name());
        }
    }

    private void validateDeployment(UUID deploymentId, boolean requireActive) {
        if (deploymentId == null) return;
        ModelDeployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> AiAgentExceptions.agentDefaultDeploymentNotFound(deploymentId));
        if (requireActive && deployment.status() != ModelDeploymentStatus.ACTIVE) {
            throw AiAgentExceptions.agentDefaultDeploymentNotActive(deployment.status().name());
        }
    }
}
