package com.company.scopery.modules.workspace.team.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.command.AddTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.command.RemoveTeamMemberCommand;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamMemberQuery;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.domain.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamMemberApplicationService {

    private final TeamApplicationService teamApplicationService;
    private final TeamMemberRepository teamMemberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceActivityLogger activityLogger;

    public TeamMemberApplicationService(TeamApplicationService teamApplicationService,
                                         TeamMemberRepository teamMemberRepository,
                                         WorkspaceMemberRepository workspaceMemberRepository,
                                         WorkspaceActivityLogger activityLogger) {
        this.teamApplicationService = teamApplicationService;
        this.teamMemberRepository = teamMemberRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public TeamMemberResponse addTeamMember(AddTeamMemberCommand command) {
        WorkspaceTeam team = teamApplicationService.findTeamInWorkspaceOrThrow(command.teamId(), command.workspaceId());

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

    @Transactional
    public void removeTeamMember(RemoveTeamMemberCommand command) {
        teamApplicationService.findTeamInWorkspaceOrThrow(command.teamId(), command.workspaceId());

        if (!teamMemberRepository.existsByTeamIdAndUserId(command.teamId(), command.userId())) {
            throw WorkspaceExceptions.teamMemberNotFound(command.teamId(), command.userId());
        }

        teamMemberRepository.delete(command.teamId(), command.userId());

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM_MEMBER, command.teamId(),
                WorkspaceActivityActions.REMOVE_TEAM_MEMBER,
                "Team member removed: userId=" + command.userId() + " from teamId=" + command.teamId());
    }

    @Transactional(readOnly = true)
    public Page<TeamMemberResponse> searchTeamMembers(SearchTeamMemberQuery query) {
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, WorkspaceSortFields.CREATED_AT));
        return teamMemberRepository.findByTeamIdPageable(query.teamId(), pageable)
                .map(TeamMemberResponse::from);
    }
}
