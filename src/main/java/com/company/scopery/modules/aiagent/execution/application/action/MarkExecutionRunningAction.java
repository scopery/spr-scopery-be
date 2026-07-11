package com.company.scopery.modules.aiagent.execution.application.action;

import com.company.scopery.modules.aiagent.execution.application.command.MarkExecutionRunningCommand;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class MarkExecutionRunningAction {

    private final ExecutionLogRepository executionLogRepository;
    private final EventConfigRepository eventConfigRepository;
    private final EventDefinitionRepository eventDefinitionRepository;
    private final AgentRepository agentRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final ModelDeploymentRepository modelDeploymentRepository;
    private final AiAgentActivityLogger activityLogger;
    private final ExecutionLifecycleWriteGuard lifecycleWriteGuard;

    public MarkExecutionRunningAction(ExecutionLogRepository executionLogRepository,
                                       EventConfigRepository eventConfigRepository,
                                       EventDefinitionRepository eventDefinitionRepository,
                                       AgentRepository agentRepository,
                                       PromptVersionRepository promptVersionRepository,
                                       ModelDeploymentRepository modelDeploymentRepository,
                                       AiAgentActivityLogger activityLogger,
                                       ExecutionLifecycleWriteGuard lifecycleWriteGuard) {
        this.executionLogRepository = executionLogRepository;
        this.eventConfigRepository = eventConfigRepository;
        this.eventDefinitionRepository = eventDefinitionRepository;
        this.agentRepository = agentRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.modelDeploymentRepository = modelDeploymentRepository;
        this.activityLogger = activityLogger;
        this.lifecycleWriteGuard = lifecycleWriteGuard;
    }

    @Transactional
    public ExecutionLogDetailResponse execute(MarkExecutionRunningCommand command) {
        lifecycleWriteGuard.verifyWritable();
        ExecutionLog log = executionLogRepository.findById(command.id())
                .orElseThrow(() -> AiAgentExceptions.executionLogNotFound(command.id()));
        if (log.status().isTerminal()) {
            throw AiAgentExceptions.terminalExecutionLogCannotBeUpdated(log.status().name());
        }
        if (log.status() != ExecutionStatus.PENDING) {
            throw AiAgentExceptions.invalidExecutionStatusTransition(log.status().name(), "RUNNING");
        }
        log.markRunning();
        ExecutionLog saved = executionLogRepository.save(log);

        activityLogger.logSuccess(AiAgentEntityTypes.EXECUTION_LOG, saved.id(),
                AiAgentActivityActions.MARK_EXECUTION_RUNNING,
                "Execution marked RUNNING: id=" + saved.id());

        return buildDetailResponse(saved);
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
