package com.company.scopery.modules.workspace.team.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateTeamAction {

    private final TeamRepository teamRepository;
    private final WorkspaceActivityLogger activityLogger;

    public UpdateTeamAction(TeamRepository teamRepository, WorkspaceActivityLogger activityLogger) {
        this.teamRepository = teamRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TeamResponse execute(UpdateTeamCommand command) {
        WorkspaceTeam team = findTeamInWorkspaceOrThrow(command.teamId(), command.workspaceId());
        if (team.status() == TeamStatus.ARCHIVED) {
            throw WorkspaceExceptions.teamArchivedCannotBeUpdated(team.code().value());
        }
        WorkspaceTeam updated = team.update(command.name(), command.description());
        WorkspaceTeam saved = teamRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.UPDATE_TEAM,
                "Team updated: " + saved.code().value());

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
