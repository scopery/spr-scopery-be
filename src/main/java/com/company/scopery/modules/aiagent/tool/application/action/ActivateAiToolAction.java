package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.ActivateAiToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolDetailResponse;
import com.company.scopery.modules.aiagent.tool.domain.enums.AiToolStatus;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermissionRepository;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateAiToolAction {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentActivityLogger activityLogger;

    public ActivateAiToolAction(AiToolRepository toolRepository,
                                AiToolPermissionRepository permissionRepository,
                                AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiToolDetailResponse execute(ActivateAiToolCommand command) {
        AiTool tool = findOrThrow(command.id());
        if (tool.status() == AiToolStatus.DEPRECATED) {
            throw AiAgentExceptions.deprecatedAiToolCannotBeActivated(tool.code().value());
        }
        tool.activate();
        AiTool saved = toolRepository.save(tool);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL, saved.id(),
                AiAgentActivityActions.ACTIVATE_AI_TOOL,
                "AI tool activated: " + saved.code().value());
        return AiToolDetailResponse.from(saved, permissionRepository.findByToolId(saved.id()));
    }

    private AiTool findOrThrow(UUID id) {
        return toolRepository.findById(id).orElseThrow(() -> AiAgentExceptions.aiToolNotFound(id));
    }
}
