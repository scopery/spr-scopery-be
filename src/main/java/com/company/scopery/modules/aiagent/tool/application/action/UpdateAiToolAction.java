package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.UpdateAiToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolDetailResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolMutationType;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermissionRepository;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import com.company.scopery.modules.aiagent.shared.util.AiAgentEnumParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateAiToolAction {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentActivityLogger activityLogger;

    public UpdateAiToolAction(AiToolRepository toolRepository,
                              AiToolPermissionRepository permissionRepository,
                              AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiToolDetailResponse execute(UpdateAiToolCommand command) {
        AiTool tool = findOrThrow(command.id());
        AiToolMutationType mutationType = AiAgentEnumParser.parseRequired(
                AiToolMutationType.class, command.mutationType(),
                AiAgentErrorCatalog.INVALID_AI_TOOL_MUTATION_TYPE.code(), "mutationType");
        tool.update(command.name(), command.description(), command.category(),
                mutationType, command.requiresHumanApproval());
        AiTool saved = toolRepository.save(tool);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL, saved.id(),
                AiAgentActivityActions.UPDATE_AI_TOOL,
                "AI tool updated: " + saved.code().value());
        return AiToolDetailResponse.from(saved, permissionRepository.findByToolId(saved.id()));
    }

    private AiTool findOrThrow(UUID id) {
        return toolRepository.findById(id).orElseThrow(() -> AiAgentExceptions.aiToolNotFound(id));
    }
}
