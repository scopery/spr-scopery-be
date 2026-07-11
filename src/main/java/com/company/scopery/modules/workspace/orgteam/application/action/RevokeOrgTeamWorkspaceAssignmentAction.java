package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.orgteam.application.command.RevokeOrgTeamWorkspaceAssignmentCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamWorkspaceAssignmentResponse;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignmentRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RevokeOrgTeamWorkspaceAssignmentAction {

    private final OrgTeamWorkspaceAssignmentRepository assignmentRepository;
    private final OrgTeamRepository orgTeamRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;

    public RevokeOrgTeamWorkspaceAssignmentAction(OrgTeamWorkspaceAssignmentRepository assignmentRepository,
                                                   OrgTeamRepository orgTeamRepository,
                                                   CurrentUserAuthorizationService currentUserAuthorizationService,
                                                   WorkspaceIamIntegrationService iamIntegrationService,
                                                   WorkspaceActivityLogger activityLogger) {
        this.assignmentRepository = assignmentRepository;
        this.orgTeamRepository = orgTeamRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgTeamWorkspaceAssignmentResponse execute(RevokeOrgTeamWorkspaceAssignmentCommand command) {
        OrgTeamWorkspaceAssignment assignment = assignmentRepository.findById(command.assignmentId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamWorkspaceAssignmentNotFound(command.assignmentId()));

        OrgTeam team = orgTeamRepository.findById(assignment.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(assignment.teamId()));
        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamWorkspaceAssignmentNotFound(command.assignmentId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_MANAGE);

        OrgTeamWorkspaceAssignment revoked = assignment.revoke();
        OrgTeamWorkspaceAssignment saved = assignmentRepository.save(revoked);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM_WORKSPACE_ASSIGNMENT, saved.id(),
                WorkspaceActivityActions.REVOKE_ORG_TEAM_WS_ASSIGNMENT,
                "Org team workspace assignment revoked: id=" + saved.id());

        return OrgTeamWorkspaceAssignmentResponse.from(saved);
    }
}
