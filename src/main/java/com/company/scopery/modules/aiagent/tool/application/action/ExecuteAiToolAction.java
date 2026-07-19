package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.ExecuteAiToolCommand;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolExecutionContext;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolHandlerRegistry;
import com.company.scopery.modules.aiagent.tool.application.port.AiToolResult;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolExecutionResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiAgentToolBindingStatus;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolApprovalState;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.*;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class ExecuteAiToolAction {

    private final AiToolRepository toolRepository;
    private final AiAgentToolBindingRepository bindingRepository;
    private final AiToolExecutionRepository executionRepository;
    private final AiAgentActivityLogger activityLogger;
    private final AiToolHandlerRegistry handlerRegistry;

    public ExecuteAiToolAction(AiToolRepository toolRepository,
                               AiAgentToolBindingRepository bindingRepository,
                               AiToolExecutionRepository executionRepository,
                               AiAgentActivityLogger activityLogger,
                               AiToolHandlerRegistry handlerRegistry) {
        this.toolRepository = toolRepository;
        this.bindingRepository = bindingRepository;
        this.executionRepository = executionRepository;
        this.activityLogger = activityLogger;
        this.handlerRegistry = handlerRegistry;
    }

    @Transactional
    public AiToolExecutionResponse execute(ExecuteAiToolCommand command) {
        AiTool tool = toolRepository.findById(command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolNotFound(command.toolId()));
        if (tool.status() != AiToolStatus.ACTIVE) {
            throw AiAgentExceptions.aiToolNotActive(tool.code().value());
        }

        if (command.agentId() != null) {
            AiAgentToolBinding binding = bindingRepository.findByAgentIdAndToolId(command.agentId(), tool.id())
                    .orElseThrow(() -> AiAgentExceptions.aiToolBindingNotFound(command.agentId(), tool.id()));
            if (binding.status() != AiAgentToolBindingStatus.ACTIVE) {
                throw AiAgentExceptions.aiToolBindingNotActive(command.agentId(), tool.id());
            }
        }

        AiToolApprovalState approvalState = tool.mutationType() == AiToolMutationType.MUTATION
                && tool.requiresHumanApproval()
                ? AiToolApprovalState.PENDING
                : AiToolApprovalState.NOT_REQUIRED;

        AiToolExecution execution = AiToolExecution.start(
                tool.id(), command.agentId(), null, command.inputSummary(), approvalState);

        if (approvalState == AiToolApprovalState.PENDING) {
            execution.markDenied("Human approval required for mutation tool; live approval workflow not implemented");
        } else if (handlerRegistry.isKnown(tool.code().value())) {
            AiToolExecutionContext ctx = new AiToolExecutionContext(
                    command.actorId(), command.workspaceId(), command.projectId(),
                    command.aclTokens() != null ? command.aclTokens() : List.of(),
                    null, null, null);
            Map<String, Object> arguments = command.arguments() != null ? command.arguments() : Map.of();
            AiToolResult result = handlerRegistry.dispatch(tool.code().value(), arguments, ctx);
            if (result.success()) {
                execution.markSucceeded("Tool executed: " + result.resultCount() + " results");
            } else {
                execution.markFailed(result.errorCode() + ": " + result.errorSummary());
            }
        } else {
            execution.markNoOp("Tool registered and authorized; live tool handler not implemented (registry stub)");
        }

        AiToolExecution saved = executionRepository.save(execution);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL_EXECUTION, saved.id(),
                AiAgentActivityActions.EXECUTE_AI_TOOL,
                "AI tool execution logged: " + tool.code().value() + " status=" + saved.status().name());
        return AiToolExecutionResponse.from(saved);
    }
}
