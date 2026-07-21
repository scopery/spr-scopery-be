package com.company.scopery.modules.aiaction.application.action;

import com.company.scopery.modules.aiaction.application.command.PauseAiActionExecutionCommand;
import com.company.scopery.modules.aiaction.application.response.AiActionControlCommandResponse;
import com.company.scopery.modules.aiaction.execution.domain.enums.AiActionControlCommandType;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecution;
import com.company.scopery.modules.aiaction.execution.domain.model.AiActionExecutionRepository;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionControlCommand;
import com.company.scopery.modules.aiaction.realtime.domain.model.AiActionControlCommandRepository;
import com.company.scopery.modules.aiaction.shared.error.AiActionExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PauseAiActionExecutionAction {

    private final AiActionExecutionRepository executionRepository;
    private final AiActionControlCommandRepository controlCommandRepository;

    public PauseAiActionExecutionAction(AiActionExecutionRepository executionRepository,
                                        AiActionControlCommandRepository controlCommandRepository) {
        this.executionRepository = executionRepository;
        this.controlCommandRepository = controlCommandRepository;
    }

    @Transactional
    public AiActionControlCommandResponse execute(PauseAiActionExecutionCommand command) {
        AiActionExecution execution = executionRepository.findById(command.executionId())
                .orElseThrow(() -> AiActionExceptions.executionNotFound(command.executionId()));

        if (!execution.isActive()) {
            throw AiActionExceptions.executionInvalidStatus(execution.id(), execution.status().name());
        }

        if (execution.executionVersion() != command.expectedVersion()) {
            throw AiActionExceptions.confirmationInvalid();
        }

        AiActionControlCommand cmd = AiActionControlCommand.create(
                execution.id(), AiActionControlCommandType.PAUSE,
                command.actorId(), command.expectedVersion(), command.idempotencyKey());

        AiActionControlCommand saved = controlCommandRepository.save(cmd);

        return new AiActionControlCommandResponse(
                saved.id(), saved.executionId(), saved.commandType().name(),
                saved.status().name(), saved.createdAt());
    }
}
