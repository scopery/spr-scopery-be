package com.company.scopery.modules.aiagent.tool.application.action;

import com.company.scopery.modules.aiagent.tool.application.command.AddAiToolPermissionCommand;
import com.company.scopery.modules.aiagent.tool.application.response.AiToolPermissionResponse;
import com.company.scopery.modules.aiagent.tool.domain.model.AiTool;
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
public class AddAiToolPermissionAction {

    private final AiToolRepository toolRepository;
    private final AiToolPermissionRepository permissionRepository;
    private final AiAgentActivityLogger activityLogger;

    public AddAiToolPermissionAction(AiToolRepository toolRepository,
                                     AiToolPermissionRepository permissionRepository,
                                     AiAgentActivityLogger activityLogger) {
        this.toolRepository = toolRepository;
        this.permissionRepository = permissionRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public AiToolPermissionResponse execute(AddAiToolPermissionCommand command) {
        AiTool tool = toolRepository.findById(command.toolId())
                .orElseThrow(() -> AiAgentExceptions.aiToolNotFound(command.toolId()));
        if (permissionRepository.existsByToolIdAndPermissionCode(tool.id(), command.permissionCode().trim())) {
            throw AiAgentExceptions.aiToolPermissionAlreadyExists(tool.code().value(), command.permissionCode());
        }
        AiToolPermission saved = permissionRepository.save(
                AiToolPermission.create(tool.id(), command.permissionCode(), command.description()));
        activityLogger.logSuccess(AiAgentEntityTypes.AI_TOOL_PERMISSION, saved.id(),
                AiAgentActivityActions.ADD_AI_TOOL_PERMISSION,
                "Permission " + saved.permissionCode() + " added to tool " + tool.code().value());
        return AiToolPermissionResponse.from(saved);
    }
}
