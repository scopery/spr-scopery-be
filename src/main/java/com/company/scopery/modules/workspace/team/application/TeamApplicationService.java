package com.company.scopery.modules.workspace.team.application;

import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceSortFields;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.util.WorkspaceEnumParser;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.command.UpdateTeamCommand;
import com.company.scopery.modules.workspace.team.application.query.SearchTeamQuery;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.TeamCode;
import com.company.scopery.modules.workspace.team.domain.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.TeamStatus;
import com.company.scopery.modules.workspace.team.domain.WorkspaceTeam;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TeamApplicationService {

    private final TeamRepository teamRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public TeamApplicationService(TeamRepository teamRepository,
                                   WorkspaceRepository workspaceRepository,
                                   WorkspaceActivityLogger activityLogger,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService) {
        this.teamRepository = teamRepository;
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional
    public TeamResponse createTeam(CreateTeamCommand command) {
        UUID creatorUserId = currentUserService.resolveCurrentUser().id();

        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        iamIntegrationService.requireWorkspaceAccess(command.workspaceId(), creatorUserId, "MANAGE_TEAM");

        TeamCode code = TeamCode.of(command.code());

        if (teamRepository.existsByWorkspaceIdAndCode(command.workspaceId(), code)) {
            throw WorkspaceExceptions.teamCodeAlreadyExists(code.value());
        }

        WorkspaceTeam team = WorkspaceTeam.create(command.workspaceId(), command.name(), code, command.description());
        WorkspaceTeam saved = teamRepository.save(team);

        try {
            iamIntegrationService.bootstrapTeamAccess(saved.id(), command.workspaceId(), saved.name(), creatorUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("TEAM", saved.id());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.CREATE_TEAM,
                "Team created: " + saved.code().value());

        return TeamResponse.from(saved);
    }

    @Transactional
    public TeamResponse updateTeam(UpdateTeamCommand command) {
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

    @Transactional
    public TeamResponse activateTeam(UUID workspaceId, UUID teamId) {
        WorkspaceTeam team = findTeamInWorkspaceOrThrow(teamId, workspaceId);
        WorkspaceTeam activated = team.activate();
        WorkspaceTeam saved = teamRepository.save(activated);

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.ACTIVATE_TEAM,
                "Team activated: " + saved.code().value());

        return TeamResponse.from(saved);
    }

    @Transactional
    public TeamResponse archiveTeam(UUID workspaceId, UUID teamId) {
        WorkspaceTeam team = findTeamInWorkspaceOrThrow(teamId, workspaceId);
        WorkspaceTeam archived = team.archive();
        WorkspaceTeam saved = teamRepository.save(archived);

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.ARCHIVE_TEAM,
                "Team archived: " + saved.code().value());

        return TeamResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeam(UUID workspaceId, UUID teamId) {
        return TeamResponse.from(findTeamInWorkspaceOrThrow(teamId, workspaceId));
    }

    @Transactional(readOnly = true)
    public Page<TeamResponse> searchTeams(SearchTeamQuery query) {
        TeamStatus status = WorkspaceEnumParser.parseOptional(
                TeamStatus.class, query.status(),
                WorkspaceErrorCatalog.INVALID_TEAM_STATUS.code(), "status");
        var pageable = PageRequestUtils.of(query.page(), query.size(),
                Sort.by(Sort.Direction.DESC, WorkspaceSortFields.CREATED_AT));
        return teamRepository.findAll(query.workspaceId(), status, pageable)
                .map(TeamResponse::from);
    }

    WorkspaceTeam findTeamInWorkspaceOrThrow(UUID teamId, UUID workspaceId) {
        WorkspaceTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> WorkspaceExceptions.teamNotFound(teamId));
        if (!team.workspaceId().equals(workspaceId)) {
            throw WorkspaceExceptions.teamNotFound(teamId);
        }
        return team;
    }
}
