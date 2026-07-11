package com.company.scopery.modules.iam.roleassignment.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.roleassignment.application.command.DeactivateIamRoleAssignmentCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class DeactivateIamRoleAssignmentAction {

    private final IamRoleAssignmentRepository assignmentRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public DeactivateIamRoleAssignmentAction(IamRoleAssignmentRepository assignmentRepository,
                                             CurrentUserAuthorizationService currentUserAuthorizationService,
                                             WorkspaceIamIntegrationService workspaceIamIntegrationService,
                                             IamSystemAuthorizationService systemAuthorizationService,
                                             IamActivityLogger activityLogger) {
        this.assignmentRepository = assignmentRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleAssignmentResponse execute(DeactivateIamRoleAssignmentCommand command) {
        IamRoleAssignment assignment = assignmentRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamRoleAssignmentNotFound(command.id()));

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        if (assignment.workspaceId() != null) {
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    assignment.workspaceId(), actorId, IamAuthorities.WORKSPACE_ROLE_ASSIGN);
        } else {
            systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_ROLE.legacyRightCode());
        }

        IamRoleAssignment saved = assignmentRepository.save(assignment.deactivate());

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_ROLE_ASSIGNMENT,
                "Role assignment deactivated: " + saved.id());

        return IamRoleAssignmentResponse.from(saved);
    }
}
