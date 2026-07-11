package com.company.scopery.modules.workspace.team.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.team.application.command.CreateTeamCommand;
import com.company.scopery.modules.workspace.team.application.response.TeamResponse;
import com.company.scopery.modules.workspace.team.domain.valueobject.TeamCode;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import com.company.scopery.modules.workspace.team.domain.model.WorkspaceTeam;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateTeamAction {

    private final TeamRepository teamRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public CreateTeamAction(TeamRepository teamRepository,
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
    public TeamResponse execute(CreateTeamCommand command) {
        UUID creatorUserId = currentUserService.resolveCurrentUser().id();

        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        iamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), creatorUserId, IamAuthorities.WORKSPACE_MANAGE_TEAM);

        TeamCode code = TeamCode.of(command.code());

        if (teamRepository.existsByWorkspaceIdAndCode(command.workspaceId(), code)) {
            throw WorkspaceExceptions.teamCodeAlreadyExists(code.value());
        }

        WorkspaceTeam team = WorkspaceTeam.create(command.workspaceId(), command.name(), code, command.description());
        WorkspaceTeam saved = teamRepository.save(team);

        try {
            iamIntegrationService.bootstrapTeamAccess(saved.id(), command.workspaceId(), saved.name(), creatorUserId);
        } catch (Exception e) {
            throw WorkspaceExceptions.workspaceIamBootstrapFailed("TEAM", saved.id(), e.getMessage());
        }

        activityLogger.logSuccess(WorkspaceEntityTypes.TEAM, saved.id(),
                WorkspaceActivityActions.CREATE_TEAM,
                "Team created: " + saved.code().value());

        return TeamResponse.from(saved);
    }
}
