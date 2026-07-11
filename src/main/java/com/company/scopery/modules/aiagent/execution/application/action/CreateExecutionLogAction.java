package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.modules.aiagent.agent.domain.model.AgentRepository;
import com.company.scopery.modules.aiagent.deployment.domain.model.ModelDeploymentRepository;
import com.company.scopery.modules.aiagent.eventconfig.domain.model.EventConfigRepository;
import com.company.scopery.modules.aiagent.execution.application.command.CreateExecutionLogCommand;
import com.company.scopery.modules.aiagent.execution.application.response.ExecutionLogResponse;
import com.company.scopery.modules.aiagent.execution.domain.enums.ExecutionTriggerSource;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLog;
import com.company.scopery.modules.aiagent.execution.domain.model.ExecutionLogRepository;
import com.company.scopery.modules.aiagent.execution.domain.valueobject.ExecutionRequestId;
import com.company.scopery.modules.aiagent.execution.application.guard.ExecutionLifecycleWriteGuard;
import com.company.scopery.modules.aiagent.prompt.domain.model.PromptVersionRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateExecutionLogAction {

    private final ExecutionLogRepository executionLogRepository;
    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final ObjectMapper objectMapper;
    private final ExecutionLifecycleWriteGuard lifecycleWriteGuard;

    public CreateExecutionLogAction(ExecutionLogRepository executionLogRepository,
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
    public ExecutionLogResponse execute(CreateExecutionLogCommand command) {
        lifecycleWriteGuard.verifyWritable();
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

    private void validateJson(String json) {
        try {
            objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw AiAgentExceptions.invalidExecutionMetadataJson();
        }
    }

    private void validateDependencies(java.util.UUID eventConfigId, java.util.UUID eventDefinitionId,
                                       java.util.UUID agentId, java.util.UUID promptVersionId,
                                       java.util.UUID modelDeploymentId) {
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
}
