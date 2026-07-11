package com.company.scopery.modules.iam.roleassignment.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AssignIamRoleAction {

    private final IamRoleAssignmentRepository assignmentRepository;
    private final IamRoleRepository roleRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceIamIntegrationService workspaceIamIntegrationService;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public AssignIamRoleAction(IamRoleAssignmentRepository assignmentRepository,
                               IamRoleRepository roleRepository,
                               WorkspaceMemberRepository workspaceMemberRepository,
                               CurrentUserAuthorizationService currentUserAuthorizationService,
                               WorkspaceIamIntegrationService workspaceIamIntegrationService,
                               IamSystemAuthorizationService systemAuthorizationService,
                               IamActivityLogger activityLogger) {
        this.assignmentRepository = assignmentRepository;
        this.roleRepository = roleRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.workspaceIamIntegrationService = workspaceIamIntegrationService;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleAssignmentResponse execute(AssignRoleCommand command) {
        RoleAssigneeType assigneeType = IamEnumParser.parseRequired(
                RoleAssigneeType.class, command.assigneeType(),
                IamErrorCatalog.INVALID_ROLE_ASSIGNEE_TYPE.code(), "assigneeType");

        var role = roleRepository.findById(command.roleId())
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(command.roleId()));

        if (role.status() != IamRoleStatus.ACTIVE) {
            throw IamExceptions.iamRoleNotActiveForAssignment(role.code().value());
        }

        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        if (command.workspaceId() != null) {
            workspaceIamIntegrationService.requireWorkspaceAccess(
                    command.workspaceId(), actorId, IamAuthorities.WORKSPACE_ROLE_ASSIGN);
        } else {
            systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_ROLE.legacyRightCode());
        }

        if (assigneeType == RoleAssigneeType.USER && command.workspaceId() != null) {
            if (!workspaceMemberRepository.isActiveMember(command.workspaceId(), command.assigneeId())) {
                throw IamExceptions.iamRoleAssignmentRequiresWorkspaceMember(
                        command.assigneeId(), command.workspaceId());
            }
        }

        if (assignmentRepository.existsActiveAssignment(
                assigneeType, command.assigneeId(), command.roleId(), command.workspaceId())) {
            throw IamExceptions.iamRoleAssignmentAlreadyExists(command.roleId(), command.assigneeId());
        }

        IamRoleAssignment assignment = IamRoleAssignment.create(
                assigneeType, command.assigneeId(), command.roleId(),
                command.workspaceId(), actorId);
        IamRoleAssignment saved = assignmentRepository.save(assignment);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                IamActivityActions.ASSIGN_IAM_ROLE,
                "Role " + command.roleId() + " assigned to " + assigneeType + " " + command.assigneeId());

        return IamRoleAssignmentResponse.from(saved);
    }
}
