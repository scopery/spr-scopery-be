package com.company.scopery.modules.workspace.workspace.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.application.response.WorkspaceResponse;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateWorkspaceAction {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public ActivateWorkspaceAction(WorkspaceRepository workspaceRepository,
                                    WorkspaceActivityLogger activityLogger) {
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceResponse execute(UUID id) {
        Workspace ws = findOrThrow(id);

        if (ws.status() == WorkspaceStatus.ARCHIVED) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        Workspace activated = ws.activate();
        Workspace saved = workspaceRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE, saved.id(),
                WorkspaceActivityActions.ACTIVATE_WORKSPACE,
                "Workspace activated: " + saved.code().value());

        return WorkspaceResponse.from(saved);
    }

    private Workspace findOrThrow(UUID id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(id));
    }
}
