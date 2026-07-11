package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orgteam.application.command.RemoveOrgTeamMemberCommand;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveOrgTeamMemberAction {

    private final OrgTeamRepository orgTeamRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public RemoveOrgTeamMemberAction(OrgTeamRepository orgTeamRepository,
                                      OrgTeamMemberRepository orgTeamMemberRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceIamIntegrationService iamIntegrationService,
                                      WorkspaceActivityLogger activityLogger) {
        this.orgTeamRepository = orgTeamRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveOrgTeamMemberCommand command) {
        OrgTeam team = orgTeamRepository.findById(command.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(command.teamId()));

        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamNotFound(command.teamId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_MEMBER_REMOVE);

        if (!orgTeamMemberRepository.existsByTeamIdAndUserId(command.teamId(), command.userId())) {
            throw WorkspaceExceptions.orgTeamMemberNotFound(command.teamId(), command.userId());
        }

        orgTeamMemberRepository.deleteByTeamIdAndUserId(command.teamId(), command.userId());

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM_MEMBER, command.teamId(),
                WorkspaceActivityActions.REMOVE_ORG_TEAM_MEMBER,
                "Member removed from org team: userId=" + command.userId());
    }
}
