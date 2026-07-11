package com.company.scopery.modules.iam.roleassignment.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.roleassignment.application.command.ActivateIamRoleAssignmentCommand;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.command.DeactivateIamRoleAssignmentCommand;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.application.service.IamRoleAssignmentQueryService;
import com.company.scopery.modules.iam.roleassignment.domain.enums.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.enums.RoleAssigneeType;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignIamRoleActionTest {

    @Mock private IamRoleAssignmentRepository assignmentRepository;
    @Mock private IamRoleRepository roleRepository;
    @Mock private WorkspaceMemberRepository workspaceMemberRepository;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private WorkspaceIamIntegrationService workspaceIamIntegrationService;
    @Mock private IamSystemAuthorizationService systemAuthorizationService;
    @Mock private IamActivityLogger activityLogger;

    private AssignIamRoleAction assignAction;
    private ActivateIamRoleAssignmentAction activateAction;
    private DeactivateIamRoleAssignmentAction deactivateAction;
    private IamRoleAssignmentQueryService queryService;

    private static final UUID ROLE_ID = UUID.randomUUID();
    private static final UUID ASSIGNEE_ID = UUID.randomUUID();
    private static final UUID WORKSPACE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        assignAction = new AssignIamRoleAction(assignmentRepository, roleRepository,
                workspaceMemberRepository, currentUserService, workspaceIamIntegrationService,
                systemAuthorizationService, activityLogger);
        activateAction = new ActivateIamRoleAssignmentAction(assignmentRepository, currentUserService,
                workspaceIamIntegrationService, systemAuthorizationService, activityLogger);
        deactivateAction = new DeactivateIamRoleAssignmentAction(assignmentRepository, currentUserService,
                workspaceIamIntegrationService, systemAuthorizationService, activityLogger);
        queryService = new IamRoleAssignmentQueryService(assignmentRepository);

        IamUser actor = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(actor);
    }

    @Test
    void assignRole_success_userAssignee_returnsResponse() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(activeRole()));
        when(assignmentRepository.existsActiveAssignment(RoleAssigneeType.USER, ASSIGNEE_ID, ROLE_ID, null))
                .thenReturn(false);
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = assignAction.execute(cmd);

        assertThat(result.assigneeType()).isEqualTo("USER");
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void assignRole_roleNotFound_throwsNotFound() {
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignAction.execute(
                new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assignRole_workspaceScopedUserNotMember_throwsUnprocessableEntity() {
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(activeRole()));
        when(workspaceMemberRepository.isActiveMember(WORKSPACE_ID, ASSIGNEE_ID)).thenReturn(false);

        assertThatThrownBy(() -> assignAction.execute(
                new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, WORKSPACE_ID)))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_REQUIRES_WORKSPACE_MEMBER.code()));
    }

    @Test
    void activateAssignment_success_returnsActiveStatus() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(inactiveAssignment(id)));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = activateAction.execute(new ActivateIamRoleAssignmentCommand(id));

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void deactivateAssignment_success_returnsInactiveStatus() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(activeAssignment(id)));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = deactivateAction.execute(new DeactivateIamRoleAssignmentCommand(id));

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void getAssignment_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryService.getAssignment(id))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private IamRole activeRole() {
        return new IamRole(ROLE_ID, IamRoleCode.of("ADMIN"), "Admin", null, IamRoleStatus.ACTIVE,
                IamRoleScope.SYSTEM, IamRoleSource.SYSTEM_BUILT_IN,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private IamRoleAssignment activeAssignment(UUID id) {
        return new IamRoleAssignment(id, RoleAssigneeType.USER, ASSIGNEE_ID, ROLE_ID, null, null,
                Instant.now(), IamRoleAssignmentStatus.ACTIVE, Instant.now(), Instant.now());
    }

    private IamRoleAssignment inactiveAssignment(UUID id) {
        return new IamRoleAssignment(id, RoleAssigneeType.USER, ASSIGNEE_ID, ROLE_ID, null, null,
                Instant.now(), IamRoleAssignmentStatus.INACTIVE, Instant.now(), Instant.now());
    }
}
