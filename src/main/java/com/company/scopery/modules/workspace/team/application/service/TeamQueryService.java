package com.company.scopery.modules.workspace.team.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamMemberQuery;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamQuery;
import com.company.scopery.modules.workspace.team.application.response.TeamMemberResponse;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.enums.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TeamQueryService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamQueryService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID workspaceId, UUID teamId) {
        return TeamResponse.from(findTeamInWorkspaceOrThrow(teamId, workspaceId));
    }

    @Transactional(readOnly = true)
    public PageResult<TeamResponse> searchTeams(SearchTeamQuery query) {
        TeamStatus status = WorkspaceEnumParser.parseOptional(
                TeamStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_TEAM_STATUS.code(), "status");
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), WorkspaceSortFields.CREATED_AT, false);
        return teamRepository.findAll(query.workspaceId(), status, pageQuery)
                .map(TeamResponse::from);
    }

    @Transactional(readOnly = true)
    public PageResult<TeamMemberResponse> searchTeamMembers(SearchTeamMemberQuery query) {
        PageQuery pageQuery = PageQuery.of(query.page(), query.size(), WorkspaceSortFields.CREATED_AT, false);
        return teamMemberRepository.findByTeamIdPageable(query.teamId(), pageQuery)
                .map(TeamMemberResponse::from);
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
