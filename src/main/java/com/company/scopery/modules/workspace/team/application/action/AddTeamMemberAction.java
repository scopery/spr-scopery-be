package com.company.scopery.modules.workspace.team.application.action;

import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.command.AddTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeamMember;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AddTeamMemberAction {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityLogger activityLogger;

    public AddTeamMemberAction(TeamRepository teamRepository,
                                TeamMemberRepository teamMemberRepository,
                                WorkspaceMemberRepository workspaceMemberRepository,
                                WorkspaceActivityLogger activityLogger) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TeamMemberResponse execute(AddTeamMemberCommand command) {
        WorkspaceTeam team = findTeamInWorkspaceOrThrow(command.teamId(), command.workspaceId());

        if (team.status() != TeamStatus.ACTIVE) {
            throw WorkspaceExceptions.teamNotActive(team.code().value());
        }

        if (!workspaceMemberRepository.isActiveMember(command.workspaceId(), command.userId())) {
            throw WorkspaceExceptions.teamMemberRequiresWorkspaceMember(command.userId());
        }

        if (teamMemberRepository.existsByTeamIdAndUserId(command.teamId(), command.userId())) {
            throw WorkspaceExceptions.teamMemberAlreadyExists(command.teamId(), command.userId());
        }

        WorkspaceTeamMember tm = WorkspaceTeamMember.create(command.teamId(), command.userId());
        WorkspaceTeamMember saved = teamMemberRepository.save(tm);

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM_MEMBER, command.teamId(),
                WorkspaceActivityActions.ADD_TEAM_MEMBER,
                "Team member added: userId=" + command.userId() + " to teamId=" + command.teamId());

        return TeamMemberResponse.from(saved);
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
