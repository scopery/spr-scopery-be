package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveWorkspaceAction {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public ArchiveWorkspaceAction(WorkspaceRepository workspaceRepository,
                                   WorkspaceActivityLogger activityLogger) {
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceResponse execute(UUID id) {
        Workspace ws = findOrThrow(id);
        Workspace archived = ws.archive();
        Workspace saved = workspaceRepository.save(archived);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.ARCHIVE_WORKSPACE,
                "Workspace archived: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    private Workspace findOrThrow(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}
