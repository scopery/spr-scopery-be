package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.modules.aiagent.execution.application.command.MarkExecutionSucceededCommand;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogDetailResponse;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionStatus;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.application.guard.ExecutionLifecycleWriteGuard;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfig;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersion;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinition;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class MarkExecutionSucceededAction {

    private final ExecutionLogRepository executionLogRepository;
    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final ObjectMapper objectMapper;
    private final ExecutionLifecycleWriteGuard lifecycleWriteGuard;

    public MarkExecutionSucceededAction(ExecutionLogRepository executionLogRepository,
                                         EventConfigRepository eventConfigRepository,
                                         EventDefinitionRepository eventDefinitionRepository,
                                         AgentRepository agentRepository,
                                         PromptVersionRepository promptVersionRepository,
                                         ModelDeploymentRepository modelDeploymentRepository,
                                         AiAgentActivityLogger activityLogger,
                                         ObjectMapper objectMapper,
                                         ExecutionLifecycleWriteGuard lifecycleWriteGuard) {
        this.executionLogRepository = executionLogRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
        this.objectMapper = objectMapper;
        this.lifecycleWriteGuard = lifecycleWriteGuard;
    }

    @Transactional
    public ExecutionLogDetailResponse execute(MarkExecutionSucceededCommand command) {
        lifecycleWriteGuard.verifyWritable();
        ExecutionLog log = executionLogRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.executionLogNotFound(command.id()));
        if (log.status().isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(log.status().name());
        }
        if (log.status() != ExecutionStatus.RUNNING) {
            throw AiAgentExceptions.invalidExecutionStatusTransition(log.status().name(), "SUCCEEDED");
        }
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
