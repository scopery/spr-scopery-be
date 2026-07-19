package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.CreateAiToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.tool.domain.valueobject.AiToolCode;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateAiToolAction {

    private final AiToolRepository toolRepository;
    private final AiAgentActivityLogger activityLogger;

    public CreateAiToolAction(AiToolRepository toolRepository, AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiToolResponse execute(CreateAiToolCommand command) {
        AiToolCode code = AiToolCode.of(command.code());
        if (toolRepository.existsByCode(code)) {
            throw AiAgentExceptions.aiToolCodeAlreadyExists(code.value());
        }
        AiToolMutationType mutationType = AiAgentEnumParser.parseRequired(
                AiToolMutationType.class, command.mutationType(),
                AiAgentErrorCatalog.INVALID_AI_TOOL_MUTATION_TYPE.code(), "mutationType");

        AiTool tool = AiTool.create(code, command.name(), command.description(), command.category(),
                mutationType, command.requiresHumanApproval());
        AiTool saved = toolRepository.save(tool);

        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL, saved.id(),
                AiAgentActivityActions.CREATE_AI_TOOL,
                "AI tool created: " + saved.code().value());
        return AiToolResponse.from(saved);
    }
}
