package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orgteam.application.command.UpdateOrgTeamCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamResponse;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UpdateOrgTeamAction {

    private final OrgTeamRepository orgTeamRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public UpdateOrgTeamAction(OrgTeamRepository orgTeamRepository,
                                CurrentUserAuthorizationService currentUserAuthorizationService,
                                WorkspaceIamIntegrationService iamIntegrationService,
                                WorkspaceActivityLogger activityLogger) {
        this.orgTeamRepository = orgTeamRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgTeamResponse execute(UpdateOrgTeamCommand command) {
        OrgTeam team = orgTeamRepository.findById(command.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(command.teamId()));

        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamNotFound(command.teamId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_UPDATE);

        if (team.status() == OrgTeamStatus.ARCHIVED) {
            throw WorkspaceExceptions.orgTeamArchivedCannotBeUpdated(team.id());
        }

        OrgTeam updated = team.update(command.name(), command.description());
        OrgTeam saved = orgTeamRepository.save(updated);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM, saved.id(),
                WorkspaceActivityActions.UPDATE_ORG_TEAM,
                "Org team updated: " + saved.code().value());

        return OrgTeamResponse.from(saved);
    }
}
