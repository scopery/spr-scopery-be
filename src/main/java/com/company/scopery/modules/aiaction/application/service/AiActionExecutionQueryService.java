package com.company.scopery.modules.aiaction.application.service;

import com.company.scopery.modules.aiaction.application.response.AiActionExecutionResponse;
import com.company.scopery.modules.aiaction.application.response.AiActionStepExecutionResponse;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionStepExecutionRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AiActionExecutionQueryService {

    private final AiActionExecutionRepository executionRepository;
    private final AiActionStepExecutionRepository stepExecutionRepository;

    public AiActionExecutionQueryService(AiActionExecutionRepository executionRepository,
                                         AiActionStepExecutionRepository stepExecutionRepository) {
        this.executionRepository = executionRepository;
        this.stepExecutionRepository = stepExecutionRepository;
    }

    @Transactional(readOnly = true)
    public AiActionExecutionResponse getExecution(UUID executionId) {
        AiActionExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> AiActionExceptions.executionNotFound(executionId));
        return toExecutionResponse(execution);
    }

    @Transactional(readOnly = true)
    public List<AiActionStepExecutionResponse> listStepExecutions(UUID executionId) {
        executionRepository.findById(executionId)
                .orElseThrow(() -> AiActionExceptions.executionNotFound(executionId));
        return stepExecutionRepository.findByExecutionIdOrderByOrdinal(executionId)
                .stream()
                .map(this::toStepExecutionResponse)
                .toList();
    }

    private AiActionExecutionResponse toExecutionResponse(AiActionExecution e) {
        return new AiActionExecutionResponse(
                e.id(), e.planId(), e.status().name(), e.executionVersion(),
                e.currentStepOrdinal(), e.succeededCount(), e.failedCount(),
                e.skippedCount(), e.compensatedCount(), e.cancelledCount(),
                e.startedAt(), e.completedAt(), e.createdAt());
    }

    private AiActionStepExecutionResponse toStepExecutionResponse(AiActionStepExecution s) {
        return new AiActionStepExecutionResponse(
                s.id(), s.stepId(), s.ordinal(), s.toolCode(), s.attempt(),
                s.status().name(), s.resultVersionToken(), s.errorCode(),
                s.retryable(), s.startedAt(), s.completedAt());
    }
}
