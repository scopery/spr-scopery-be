package com.company.scopery.modules.aiagent.execution.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.aiagent.agent.domain.Agent;
import com.company.scopery.modules.aiagent.agent.domain.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeployment;
import com.company.scopery.modules.aiagent.deployment.domain.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.*;
import com.company.scopery.modules.aiagent.execution.application.query.*;
import com.company.scopery.modules.aiagent.execution.application.response.*;
import com.company.scopery.modules.aiagent.execution.domain.*;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentSortFields;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.EventDefinitionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ExecutionLogApplicationService {

    private final ExecutionLogRepository executionLogRepository;
    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final ObjectMapper objectMapper;

    public ExecutionLogApplicationService(ExecutionLogRepository executionLogRepository,
                                          EventConfigRepository eventConfigRepository,
                                          EventDefinitionRepository eventDefinitionRepository,
                                          AgentRepository agentRepository,
                                          PromptVersionRepository promptVersionRepository,
                                          ModelDeploymentRepository modelDeploymentRepository,
                                          AiAgentActivityLogger activityLogger,
                                          ObjectMapper objectMapper) {
        this.executionLogRepository = executionLogRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ExecutionLogResponse createExecutionLog(CreateExecutionLogCommand command) {
        ExecutionRequestId requestId = ExecutionRequestId.of(command.requestId());
        if (executionLogRepository.existsByRequestId(requestId)) {
            throw AiAgentExceptions.executionLogRequestIdAlreadyExists(requestId.value());
        }

        ExecutionTriggerSource triggerSource = AiAgentEnumParser.parseRequired(
                ExecutionTriggerSource.class, command.triggerSource(),
                AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.code(), "triggerSource");

        if (command.metadata() != null) {
            validateJson(command.metadata());
        }

        validateDependencies(command.eventConfigId(), command.eventDefinitionId(),
                command.agentId(), command.promptVersionId(), command.modelDeploymentId());

        ExecutionLog log = ExecutionLog.create(requestId, command.eventConfigId(),
                command.eventDefinitionId(), command.agentId(), command.promptVersionId(),
                command.modelDeploymentId(), triggerSource, command.metadata());

        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.CREATE_EXECUTION_LOG,
                "Execution log created: requestId=" + saved.requestId().value());

        return ExecutionLogResponse.from(saved);
    }

    @Transactional
    public ExecutionLogDetailResponse markRunning(MarkExecutionRunningCommand command) {
        ExecutionLog log = findOrThrow(command.id());
        validateTransitionToRunning(log.status());
        log.markRunning();
        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.MARK_EXECUTION_RUNNING,
                "Execution marked RUNNING: id=" + saved.id());

        return buildDetailResponse(saved);
    }

    @Transactional
    public ExecutionLogDetailResponse markSucceeded(MarkExecutionSucceededCommand command) {
        ExecutionLog log = findOrThrow(command.id());
        validateTransitionToSucceeded(log.status());
        validateUsageMetrics(command.inputTokenCount(), command.outputTokenCount(),
                command.totalTokenCount(), command.estimatedCost());
        if (command.metadata() != null) {
            validateJson(command.metadata());
        }

        log.markSucceeded(command.inputTokenCount(), command.outputTokenCount(),
                command.totalTokenCount(), command.estimatedCost(),
                command.providerRequestId(), command.metadata());
        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.MARK_EXECUTION_SUCCEEDED,
                "Execution marked SUCCEEDED: id=" + saved.id());

        return buildDetailResponse(saved);
    }

    @Transactional
    public ExecutionLogDetailResponse markFailed(MarkExecutionFailedCommand command) {
        ExecutionLog log = findOrThrow(command.id());
        validateTransitionToFailed(log.status());
        validateUsageMetrics(command.inputTokenCount(), command.outputTokenCount(),
                command.totalTokenCount(), command.estimatedCost());
        if (command.metadata() != null) {
            validateJson(command.metadata());
        }

        log.markFailed(command.errorCode(), command.errorMessage(),
                command.inputTokenCount(), command.outputTokenCount(),
                command.totalTokenCount(), command.estimatedCost(),
                command.providerRequestId(), command.metadata());
        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.MARK_EXECUTION_FAILED,
                "Execution marked FAILED: id=" + saved.id());

        return buildDetailResponse(saved);
    }

    @Transactional
    public ExecutionLogDetailResponse cancelExecution(CancelExecutionCommand command) {
        ExecutionLog log = findOrThrow(command.id());
        validateTransitionToCancel(log.status());
        log.cancel();
        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.CANCEL_EXECUTION,
                "Execution cancelled: id=" + saved.id());

        return buildDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public ExecutionLogDetailResponse getExecutionLogDetail(GetExecutionLogDetailQuery query) {
        ExecutionLog log = findOrThrow(query.id());
        return buildDetailResponse(log);
    }

    @Transactional(readOnly = true)
    public Page<ExecutionLogResponse> searchExecutionLogs(SearchExecutionLogQuery query) {
        ExecutionTriggerSource triggerSource = AiAgentEnumParser.parseOptional(
                ExecutionTriggerSource.class, query.triggerSource(),
                AiAgentErrorCatalog.INVALID_EXECUTION_TRIGGER_SOURCE.code(), "triggerSource");
        ExecutionStatus status = AiAgentEnumParser.parseOptional(
                ExecutionStatus.class, query.status(),
                AiAgentErrorCatalog.INVALID_EXECUTION_STATUS.code(), "status");

        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT));

        return executionLogRepository.findAll(
                query.requestId(), query.eventConfigId(), query.eventDefinitionId(),
                query.agentId(), query.promptVersionId(), query.modelDeploymentId(),
                triggerSource, status, query.createdFrom(), query.createdTo(), pageable)
                .map(ExecutionLogResponse::from);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ExecutionLog findOrThrow(UUID id) {
        return executionLogRepository.findById(id)
                .orElseThrow(() -> AiAgentExceptions.executionLogNotFound(id));
    }

    private void validateTransitionToRunning(ExecutionStatus current) {
        if (current.isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(current.name());
        }
        if (current != ExecutionStatus.PENDING) {
            throw AiAgentExceptions.invalidExecutionStatusTransition(current.name(), "RUNNING");
        }
    }

    private void validateTransitionToSucceeded(ExecutionStatus current) {
        if (current.isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(current.name());
        }
        if (current != ExecutionStatus.RUNNING) {
            throw AiAgentExceptions.invalidExecutionStatusTransition(current.name(), "SUCCEEDED");
        }
    }

    private void validateTransitionToFailed(ExecutionStatus current) {
        if (current.isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(current.name());
        }
        if (current != ExecutionStatus.RUNNING) {
            throw AiAgentExceptions.invalidExecutionStatusTransition(current.name(), "FAILED");
        }
    }

    private void validateTransitionToCancel(ExecutionStatus current) {
        if (current.isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(current.name());
        }
    }

    private void validateUsageMetrics(Integer inputTokenCount, Integer outputTokenCount,
                                       Integer totalTokenCount, BigDecimal estimatedCost) {
        if (inputTokenCount != null && inputTokenCount < 0) {
            throw AiAgentExceptions.invalidExecutionUsageMetrics("inputTokenCount must be >= 0");
        }
        if (outputTokenCount != null && outputTokenCount < 0) {
            throw AiAgentExceptions.invalidExecutionUsageMetrics("outputTokenCount must be >= 0");
        }
        if (totalTokenCount != null && totalTokenCount < 0) {
            throw AiAgentExceptions.invalidExecutionUsageMetrics("totalTokenCount must be >= 0");
        }
        if (estimatedCost != null && estimatedCost.compareTo(BigDecimal.ZERO) < 0) {
            throw AiAgentExceptions.invalidExecutionUsageMetrics("estimatedCost must be >= 0");
        }
        if (inputTokenCount != null && outputTokenCount != null && totalTokenCount != null
                && totalTokenCount != inputTokenCount + outputTokenCount) {
            throw AiAgentExceptions.invalidExecutionUsageMetrics(
                    "totalTokenCount must equal inputTokenCount + outputTokenCount");
        }
    }

    private void validateJson(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw AiAgentExceptions.invalidExecutionMetadataJson();
        }
    }

    private void validateDependencies(UUID eventConfigId, UUID eventDefinitionId,
                                       UUID agentId, UUID promptVersionId, UUID modelDeploymentId) {
        if (eventConfigId != null && eventConfigRepository.findById(eventConfigId).isEmpty()) {
            throw AiAgentExceptions.executionLogEventConfigNotFound(eventConfigId);
        }
        if (eventDefinitionId != null && eventDefinitionRepository.findById(eventDefinitionId).isEmpty()) {
            throw AiAgentExceptions.executionLogEventDefinitionNotFound(eventDefinitionId);
        }
        if (agentRepository.findById(agentId).isEmpty()) {
            throw AiAgentExceptions.executionLogAgentNotFound(agentId);
        }
        if (promptVersionRepository.findById(promptVersionId).isEmpty()) {
            throw AiAgentExceptions.executionLogPromptVersionNotFound(promptVersionId);
        }
        if (modelDeploymentRepository.findById(modelDeploymentId).isEmpty()) {
            throw AiAgentExceptions.executionLogModelDeploymentNotFound(modelDeploymentId);
        }
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
                .map(Agent::name).orElse(null);
        Integer promptVersionNumber = promptVersionRepository.findById(log.promptVersionId())
                .map(PromptVersion::versionNumber).orElse(null);
        String modelDeploymentCode = modelDeploymentRepository.findById(log.modelDeploymentId())
                .map(ModelDeployment::code).map(c -> c.value()).orElse(null);

        return ExecutionLogDetailResponse.from(log, eventConfigCode, eventDefinitionCode,
                agentName, promptVersionNumber, modelDeploymentCode);
    }
}