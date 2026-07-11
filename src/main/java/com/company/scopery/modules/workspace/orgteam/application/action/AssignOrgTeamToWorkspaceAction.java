package com.company.scopery.modules.workspace.orgteam.application.action;

import com.company.scopery.modules.workspace.orgteam.application.command.AssignOrgTeamToWorkspaceCommand;
import com.company.scopery.modules.workspace.orgteam.application.response.OrgTeamWorkspaceAssignmentResponse;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamStatus;
import com.company.scopery.modules.workspace.orgteam.domain.enums.OrgTeamWorkspaceAssignmentStatus;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeam;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignment;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamWorkspaceAssignmentRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AssignOrgTeamToWorkspaceAction {

    private final OrgTeamRepository orgTeamRepository;
    private final OrgTeamWorkspaceAssignmentRepository assignmentRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceActivityLogger activityLogger;

    public AssignOrgTeamToWorkspaceAction(OrgTeamRepository orgTeamRepository,
                                           OrgTeamWorkspaceAssignmentRepository assignmentRepository,
                                           CurrentUserAuthorizationService currentUserAuthorizationService,
                                           WorkspaceIamIntegrationService iamIntegrationService,
                                           WorkspaceRepository workspaceRepository,
                                           WorkspaceActivityLogger activityLogger) {
        this.orgTeamRepository = orgTeamRepository;
        this.assignmentRepository = assignmentRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.iamIntegrationService = iamIntegrationService;
        this.workspaceRepository = workspaceRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgTeamWorkspaceAssignmentResponse execute(AssignOrgTeamToWorkspaceCommand command) {
        OrgTeam team = orgTeamRepository.findById(command.teamId())
                .orElseThrow(() -> WorkspaceExceptions.orgTeamNotFound(command.teamId()));

        if (!team.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgTeamNotFound(command.teamId());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.TEAM_MANAGE);

        Workspace workspace = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));
        if (!workspace.organizationId().equals(team.organizationId())) {
            throw WorkspaceExceptions.orgTeamCrossOrganizationAssignment(command.teamId(), command.workspaceId());
        }

        if (team.status() != OrgTeamStatus.ACTIVE) {
            throw WorkspaceExceptions.orgTeamNotActive(team.code().value());
        }

        if (assignmentRepository.existsByTeamIdAndWorkspaceIdAndStatus(
                command.teamId(), command.workspaceId(), OrgTeamWorkspaceAssignmentStatus.ACTIVE.name())) {
            throw WorkspaceExceptions.orgTeamWorkspaceAssignmentAlreadyExists(command.teamId(), command.workspaceId());
        }

        OrgTeamWorkspaceAssignment assignment = OrgTeamWorkspaceAssignment.create(
                command.teamId(), command.workspaceId(), actorId);
        OrgTeamWorkspaceAssignment saved = assignmentRepository.save(assignment);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_TEAM_WORKSPACE_ASSIGNMENT, saved.id(),
                WorkspaceActivityActions.ASSIGN_ORG_TEAM_TO_WORKSPACE,
                "Org team assigned to workspace: teamId=" + command.teamId() + ", workspaceId=" + command.workspaceId());

        return OrgTeamWorkspaceAssignmentResponse.from(saved);
    }
}
