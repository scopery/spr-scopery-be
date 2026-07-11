package com.company.scopery.modules.workspace.team.application.action;

import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.command.RemoveTeamMemberCommand;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveTeamMemberAction {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final WorkspaceActivityLogger activityLogger;

    public RemoveTeamMemberAction(TeamRepository teamRepository,
                                   TeamMemberRepository teamMemberRepository,
                                   WorkspaceActivityLogger activityLogger) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveTeamMemberCommand command) {
        findTeamInWorkspaceOrThrow(command.teamId(), command.workspaceId());

        if (!teamMemberRepository.existsByTeamIdAndUserId(command.teamId(), command.userId())) {
            throw WorkspaceExceptions.teamMemberNotFound(command.teamId(), command.userId());
        }

        teamMemberRepository.delete(command.teamId(), command.userId());

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM_MEMBER, command.teamId(),
                WorkspaceActivityActions.REMOVE_TEAM_MEMBER,
                "Team member removed: userId=" + command.userId() + " from teamId=" + command.teamId());
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
