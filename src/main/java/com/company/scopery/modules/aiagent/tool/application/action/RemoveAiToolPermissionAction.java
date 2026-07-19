package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.RemoveAiToolPermissionCommand;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermission;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolPermissionRepository;
import com.company.scopery.modules.aiagent.tool.domain.model.AiToolRepository;
import com.company.scopery.modules.aiagent.shared.activity.AiAgentActivityLogger;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentActivityActions;
import com.company.scopery.modules.aiagent.shared.constant.AiAgentEntityTypes;
import com.company.scopery.modules.aiagent.shared.error.AiAgentExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveAiToolPermissionAction {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentActivityLogger activityLogger;

    public RemoveAiToolPermissionAction(AiToolRepository toolRepository,
                                        AiToolPermissionRepository permissionRepository,
                                        AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveAiToolPermissionCommand command) {
        toolRepository.findById(command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolNotFound(command.toolId()));
        AiToolPermission permission = permissionRepository.findById(command.permissionId())
                .orElseThrow(() -> AiAgentExceptions.aiToolPermissionNotFound(command.permissionId()));
        if (!permission.toolId().equals(command.toolId())) {
            throw AiAgentExceptions.aiToolPermissionNotFound(command.permissionId());
        }
        permissionRepository.deleteById(permission.id());
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL_PERMISSION, permission.id(),
                AiAgentActivityActions.REMOVE_AI_TOOL_PERMISSION,
                "Permission removed: " + permission.permissionCode());
    }
}
