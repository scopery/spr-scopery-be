package com.company.scopery.modules.workspace.team.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ActivateTeamAction {

    private final TeamRepository teamRepository;
    private final WorkspaceActivityLogger activityLogger;

    public ActivateTeamAction(TeamRepository teamRepository, WorkspaceActivityLogger activityLogger) {
        this.teamRepository = teamRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TeamResponse execute(UUID workspaceId, UUID teamId) {
        WorkspaceTeam team = findTeamInWorkspaceOrThrow(teamId, workspaceId);
        WorkspaceTeam activated = team.activate();
        WorkspaceTeam saved = teamRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.ACTIVATE_TEAM,
                "Team activated: " + saved.code().value());

        return TeamResponse.from(saved);
    }

    private WorkspaceTeam findTeamInWorkspaceOrThrow(UUID teamId, UUID workspaceId) {
        WorkspaceTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> WorkspaceExceptions.teamNotFound(teamId));
        if (!team.workspaceId().equals(workspaceId)) {
            throw WorkspaceExceptions.teamNotFound(teamId);
        }
        return team;
    }
}
