package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.common.exception.BusinessException;
import com.company.scopery.modules.aiaction.application.orchestrator.AiActionExecutionOrchestrator;
import com.company.scopery.modules.aiaction.application.port.AiActionWorkerLeasePort;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionExecutionStatus;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RunAiActionPlanSyncAction {

    private static final Logger log = LoggerFactory.getLogger(RunAiActionPlanSyncAction.class);

    private final AiActionWorkerLeasePort leasePort;
    private final AiActionExecutionRepository executionRepository;
    private final AiActionStepExecutionRepository stepExecutionRepository;
    private final AiActionExecutionOrchestrator orchestrator;

    public RunAiActionPlanSyncAction(AiActionWorkerLeasePort leasePort,
                                     AiActionExecutionRepository executionRepository,
                                     AiActionStepExecutionRepository stepExecutionRepository,
                                     AiActionExecutionOrchestrator orchestrator) {
        this.leasePort = leasePort;
        this.executionRepository = executionRepository;
        this.stepExecutionRepository = stepExecutionRepository;
        this.orchestrator = orchestrator;
    }

    // No @Transactional — leasePort and orchestrator each manage their own transactions.
    public void runAndThrowIfFailed(UUID executionId) {
        String workerId = "sync-" + executionId;
        boolean claimed = leasePort.claimById(executionId, workerId, 60);
        if (!claimed) {
            log.warn("[SyncRunner] Could not claim execution {} — may already be running", executionId);
        }

        orchestrator.run(executionId);

        AiActionExecution finalExec = executionRepository.findById(executionId).orElse(null);
        if (finalExec == null) return;

        if (finalExec.status() == AiActionExecutionStatus.FAILED) {
            List<AiActionStepExecution> steps = stepExecutionRepository.findByExecutionIdOrderByOrdinal(executionId);
            String errorDetails = steps.stream()
                    .filter(s -> s.errorCode() != null)
                    .map(s -> toUserMessage(s.errorCode()))
                    .distinct()
                    .collect(Collectors.joining("; "));
            throw new BusinessException("AI_ACTION_FAILED",
                    errorDetails.isBlank() ? "Action failed to execute." : errorDetails);
        }
    }

    private static String toUserMessage(String errorCode) {
        return switch (errorCode) {
            case "NO_ACTIVE_PHASE_FOUND" ->
                "This project has no active or planned phase. Please create a project phase first before creating tasks.";
            case "MISSING_REQUIRED_INPUT" -> "Some required fields are missing.";
            case "INVALID_UUID_INPUT" -> "An ID in the action input is invalid.";
            case "INVALID_FUNCTIONAL_ITEM_TYPE" -> "Invalid functional item type.";
            case "INVALID_NFR_CATEGORY" -> "Invalid non-functional requirement category.";
            case "ADAPTER_ERROR" -> "An unexpected error occurred while executing the action.";
            default -> "Step failed: " + errorCode;
        };
    }
}
