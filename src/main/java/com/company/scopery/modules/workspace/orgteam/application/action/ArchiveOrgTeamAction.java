package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceLifecycleService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orgteam.application.command.ArchiveOrgTeamCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamResponse;
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
public class ArchiveOrgTeamAction {

    private final OrgTeamRepository orgTeamRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final IamAuthResourceLifecycleService authResourceLifecycleService;
    private final WorkspaceActivityLogger activityLogger;

    public ArchiveOrgTeamAction(OrgTeamRepository orgTeamRepository,
                                 CurrentUserAuthorizationService currentUserAuthorizationService,
                                 WorkspaceIamIntegrationService iamIntegrationService,
                                 IamAuthResourceLifecycleService authResourceLifecycleService,
                                 WorkspaceActivityLogger activityLogger) {
        this.orgTeamRepository = orgTeamRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.authResourceLifecycleService = authResourceLifecycleService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgTeamResponse execute(ArchiveOrgTeamCommand command) {
        OrgTeam team = orgTeamRepository.findById(command.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(command.teamId()));

        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamNotFound(command.teamId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_ARCHIVE);

        OrgTeam archived = team.archive();
        OrgTeam saved = orgTeamRepository.save(archived);
        authResourceLifecycleService.deactivateByRef(saved.id(), IamResourceType.TEAM);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM, saved.id(),
                WorkspaceActivityActions.ARCHIVE_ORG_TEAM,
                "Org team archived: " + saved.code().value());

        return OrgTeamResponse.from(saved);
    }
}
