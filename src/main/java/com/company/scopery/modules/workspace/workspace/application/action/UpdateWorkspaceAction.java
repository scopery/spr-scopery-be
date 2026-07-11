package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.workspace.application.command.UpdateWorkspaceCommand;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceVisibility;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateWorkspaceAction {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public UpdateWorkspaceAction(WorkspaceRepository workspaceRepository,
                                  WorkspaceActivityLogger activityLogger) {
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceResponse execute(UpdateWorkspaceCommand command) {
        Workspace ws = findOrThrow(command.id());
        if (ws.status() == WorkspaceStatus.ARCHIVED) {
            throw WorkspaceExceptions.workspaceArchivedCannotBeUpdated(ws.code().value());
        }

        WorkspaceVisibility visibility = WorkspaceEnumParser.parseOptional(
                WorkspaceVisibility.class, command.defaultVisibility(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_VISIBILITY.code(), "defaultVisibility");
        if (visibility == null) {
            visibility = ws.defaultVisibility();
        }

        WorkspaceJoinPolicy joinPolicy = WorkspaceEnumParser.parseOptional(
                WorkspaceJoinPolicy.class, command.joinPolicy(),
                WorkspaceErrorCatalog.INVALID_WORKSPACE_JOIN_POLICY.code(), "joinPolicy");
        if (joinPolicy == null) {
            joinPolicy = ws.joinPolicy();
        }

        Workspace updated = ws.update(command.name(), command.description(), visibility, joinPolicy);
        Workspace saved = workspaceRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.UPDATE_WORKSPACE,
                "Workspace updated: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    private Workspace findOrThrow(java.util.UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}
