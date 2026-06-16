package com.company.scopery.modules.aiagent.agent.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.agent.application.command.*;
import com.company.scopery.modules.aiagent.agent.application.query.*;
import com.company.scopery.modules.aiagent.agent.application.response.*;
import com.company.scopery.modules.aiagent.agent.domain.*;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentStatus;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AgentApplicationService {

    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository deploymentRepository;
    private final AiAgentActivityLogger activityLogger;

    public AgentApplicationService(AgentRepository agentRepository,
                                   ModelDeploymentRepository deploymentRepository,
                                   AiAgentActivityLogger activityLogger) {
        this.agentRepository = agentRepository;
        this.deploymentRepository = deploymentRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AgentResponse createAgent(CreateAgentCommand command) {
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

        validateDeployment(command.defaultModelDeploymentId(), true);

        Agent agent = Agent.create(command.name(), code, type, command.description(),
                command.defaultModelDeploymentId(), outputFormat);

        Agent saved = agentRepository.save(agent);

        activityLogger.logSuccess(AiAgentEntityTypes.AGENT, saved.id(),
                AiAgentActivityActions.CREATE_AGENT,
                "Agent created: " + saved.code().value());

        return AgentResponse.from(saved);
    }

    @Transactional
    public AgentDetailResponse updateAgent(UpdateAgentCommand command) {
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

    @Transactional(readOnly = true)
    public AgentDetailResponse getAgentDetail(GetAgentDetailQuery query) {
        Agent agent = findOrThrow(query.id());
        String deploymentName = loadDeploymentName(agent.defaultModelDeploymentId());
        return AgentDetailResponse.from(agent, deploymentName);
    }

    @Transactional(readOnly = true)
    public Page<AgentResponse> searchAgents(SearchAgentQuery query) {
        AgentType type = AiAgentEnumParser.parseOptional(
                AgentType.class, query.type(),
                AiAgentErrorCatalog.INVALID_AGENT_TYPE.code(), "type");
        AgentStatus status = AiAgentEnumParser.parseOptional(
                AgentStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AGENT_STATUS.code(), "status");
        AgentOutputFormat outputFormat = AiAgentEnumParser.parseOptional(
                AgentOutputFormat.class, query.outputFormat(),
                AiAgentErrorCatalog.INVALID_AGENT_OUTPUT_FORMAT.code(), "outputFormat");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return agentRepository
                .findAll(query.keyword(), type, status, outputFormat, pageable)
                .map(AgentResponse::from);
    }

    @Transactional
    public AgentDetailResponse activateAgent(ActivateAgentCommand command) {
        Agent agent = findOrThrow(command.id());

        if (agent.status() == AgentStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedAgentCannotBeActivated(agent.code().value());
        }

        validateDeployment(agent.defaultModelDeploymentId(), true);

        agent.activate();
        Agent saved = agentRepository.save(agent);

        activityLogger.logSuccess(AiAgentEntityTypes.AGENT, saved.id(),
                AiAgentActivityActions.ACTIVATE_AGENT,
                "Agent activated: " + saved.code().value());

        String deploymentName = loadDeploymentName(saved.defaultModelDeploymentId());
        return AgentDetailResponse.from(saved, deploymentName);
    }

    @Transactional
    public AgentDetailResponse deactivateAgent(DeactivateAgentCommand command) {
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