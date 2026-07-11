package com.company.scopery.modules.aiagent.agent.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.application.query.GetAgentDetailQuery;
import com.company.scopery.modules.aiagent.agent.application.query.SearchAgentQuery;
import com.company.scopery.modules.aiagent.agent.application.response.AgentDetailResponse;
import com.company.scopery.modules.aiagent.agent.application.response.AgentResponse;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentOutputFormat;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentStatus;
import com.company.scopery.modules.aiagent.agent.domain.enums.AgentType;
import com.company.scopery.modules.aiagent.agent.domain.model.Agent;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AgentQueryService {

    private final AgentRepository agentRepository;
    private final ModelDeploymentRepository deploymentRepository;

    public AgentQueryService(AgentRepository agentRepository,
                             ModelDeploymentRepository deploymentRepository) {
        this.agentRepository = agentRepository;
        this.deploymentRepository = deploymentRepository;
    }

    @Transactional(readOnly = true)
    public AgentDetailResponse getAgentDetail(GetAgentDetailQuery query) {
        Agent agent = findOrThrow(query.id());
        String deploymentName = loadDeploymentName(agent.defaultModelDeploymentId());
        return AgentDetailResponse.from(agent, deploymentName);
    }

    @Transactional(readOnly = true)
    public PageResult<AgentResponse> searchAgents(SearchAgentQuery query) {
        AgentType type = AiAgentEnumParser.parseOptional(
                AgentType.class, query.type(),
                AiAgentErrorCatalog.INVALID_AGENT_TYPE.code(), "type");
        AgentStatus status = AiAgentEnumParser.parseOptional(
                AgentStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_AGENT_STATUS.code(), "status");
        AgentOutputFormat outputFormat = AiAgentEnumParser.parseOptional(
                AgentOutputFormat.class, query.outputFormat(),
                AiAgentErrorCatalog.INVALID_AGENT_OUTPUT_FORMAT.code(), "outputFormat");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return agentRepository
                .findAll(query.keyword(), type, status, outputFormat, pageQuery)
                .map(AgentResponse::from);
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
