package com.company.scopery.modules.aiagent.execution.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.query.GetExecutionLogDetailQuery;
import com.company.scopery.modules.aiagent.execution.application.query.SearchExecutionLogQuery;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogDetailResponse;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogResponse;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExecutionLogQueryService {

    private final ExecutionLogRepository executionLogRepository;
    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;

    public ExecutionLogQueryService(ExecutionLogRepository executionLogRepository,
                                     EventConfigRepository eventConfigRepository,
                                     EventDefinitionRepository eventDefinitionRepository,
                                     AgentRepository agentRepository,
                                     PromptVersionRepository promptVersionRepository,
                                     ModelDeploymentRepository modelDeploymentRepository) {
        this.executionLogRepository = executionLogRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
    }

    @Transactional(readOnly = true)
    public ExecutionLogDetailResponse getExecutionLogDetail(GetExecutionLogDetailQuery query) {
        ExecutionLog log = executionLogRepository.findById(query.id())
                .orElseThrow(() -> AiAgentExceptions.executionLogNotFound(query.id()));
        return buildDetailResponse(log);
    }

    @Transactional(readOnly = true)
    public PageResult<ExecutionLogResponse> searchExecutionLogs(SearchExecutionLogQuery query) {
        ExecutionTriggerSource triggerSource = AiAgentEnumParser.parseOptional(
                ExecutionTriggerSource.class, query.triggerSource(),
                AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.code(), "triggerSource");
        ExecutionStatus status = AiAgentEnumParser.parseOptional(
                ExecutionStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_EXECUTION_STATUS.code(), "status");

        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), AiAgentSortFields.CREATED_AT, false);

        return executionLogRepository.findAll(
                query.requestId(), query.eventConfigId(), query.eventDefinitionId(),
                query.agentId(), query.promptVersionId(), query.modelDeploymentId(),
                triggerSource, status, query.createdFrom(), query.createdTo(), pageQuery)
                .map(ExecutionLogResponse::from);
    }

    private ExecutionLogDetailResponse buildDetailResponse(ExecutionLog log) {
        String eventConfigCode = null;
        if (log.eventConfigId() != null) {
            eventConfigCode = eventConfigRepository.findById(log.eventConfigId())
                    .map(EventConfig::code).map(c -> c.value()).orElse(null);
        }
        String eventDefinitionCode = null;
        if (log.eventDefinitionId() != null) {
            eventDefinitionCode = eventDefinitionRepository.findById(log.eventDefinitionId())
                    .map(EventDefinition::code).map(c -> c.value()).orElse(null);
        }
        String agentName = agentRepository.findById(log.agentId())
                .map(a -> a.name()).orElse(null);
        Integer promptVersionNumber = promptVersionRepository.findById(log.promptVersionId())
                .map(PromptVersion::versionNumber).orElse(null);
        String modelDeploymentCode = modelDeploymentRepository.findById(log.modelDeploymentId())
                .map(d -> d.code().value()).orElse(null);

        return ExecutionLogDetailResponse.from(log, eventConfigCode, eventDefinitionCode,
                agentName, promptVersionNumber, modelDeploymentCode);
    }
}
