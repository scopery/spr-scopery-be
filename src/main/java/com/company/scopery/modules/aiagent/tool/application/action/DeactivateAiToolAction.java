package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.DeactivateAiToolCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolDetailResponse;
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
public class DeactivateAiToolAction {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentActivityLogger activityLogger;

    public DeactivateAiToolAction(AiToolRepository toolRepository,
                                  AiToolPermissionRepository permissionRepository,
                                  AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiToolDetailResponse execute(DeactivateAiToolCommand command) {
        AiTool tool = findOrThrow(command.id());
        tool.deactivate();
        AiTool saved = toolRepository.save(tool);
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL, saved.id(),
                AiAgentActivityActions.DEACTIVATE_AI_TOOL,
                "AI tool deactivated: " + saved.code().value());
        return AiToolDetailResponse.from(saved, permissionRepository.findByToolId(saved.id()));
    }

    private AiTool findOrThrow(UUID id) {
        return toolRepository.findById(id).orElseThrow(() -> AiAgentExceptions.aiToolNotFound(id));
    }
}
