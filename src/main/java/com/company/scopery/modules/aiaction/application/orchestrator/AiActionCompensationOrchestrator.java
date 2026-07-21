package com.company.scopery.modules.aiaction.application.orchestrator;

import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AiActionCompensationOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AiActionCompensationOrchestrator.class);

    private final AiActionExecutionRepository executionRepository;

    public AiActionCompensationOrchestrator(AiActionExecutionRepository executionRepository) {
        this.executionRepository = executionRepository;
    }

    @Transactional
    public void compensate(UUID executionId) {
        AiActionExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> AiActionExceptions.executionNotFound(executionId));

        execution.markCompensating();
        executionRepository.save(execution);

        // TODO: iterate succeeded steps in reverse, call adapter.compensate()
        log.warn("Compensation orchestrator shell: no-op for execution {}", executionId);

        execution.markCompensated();
        executionRepository.save(execution);
    }
}
