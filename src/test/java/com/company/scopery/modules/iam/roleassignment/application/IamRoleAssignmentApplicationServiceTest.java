package com.company.scopery.modules.iam.roleassignment.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.role.domain.IamRole;
import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
import com.company.scopery.modules.iam.roleassignment.application.command.AssignRoleCommand;
import com.company.scopery.modules.iam.roleassignment.application.response.IamRoleAssignmentResponse;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentStatus;
import com.company.scopery.modules.iam.roleassignment.domain.RoleAssigneeType;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
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
class IamRoleAssignmentApplicationServiceTest {

    @Mock IamRoleAssignmentRepository assignmentRepository;
    @Mock IamRoleRepository roleRepository;
    @Mock WorkspaceMemberRepository workspaceMemberRepository;
    @Mock IamActivityLogger activityLogger;

    private IamRoleAssignmentApplicationService service;

    private static final UUID ROLE_ID    = UUID.randomUUID();
    private static final UUID ASSIGNEE_ID = UUID.randomUUID();
    private static final UUID WORKSPACE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new IamRoleAssignmentApplicationService(
                assignmentRepository, roleRepository, workspaceMemberRepository, activityLogger);
    }

    @Test
    void assignRole_success_userAssignee_returnsResponse() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(activeRole()));
        when(assignmentRepository.existsActiveAssignment(RoleAssigneeType.USER, ASSIGNEE_ID, ROLE_ID, null))
                .thenReturn(false);
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = service.assignRole(cmd);

        assertThat(result.assigneeType()).isEqualTo("USER");
        assertThat(result.assigneeId()).isEqualTo(ASSIGNEE_ID);
        assertThat(result.roleId()).isEqualTo(ROLE_ID);
        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void assignRole_roleNotFound_throwsNotFound() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignRole(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getHttpStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void assignRole_roleNotActive_throwsUnprocessableEntity() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(inactiveRole()));

        assertThatThrownBy(() -> service.assignRole(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(appEx.getErrorCode()).isEqualTo(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_ROLE_NOT_ACTIVE.code());
                });
    }

    @Test
    void assignRole_alreadyExists_throwsConflict() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, null, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(activeRole()));
        when(assignmentRepository.existsActiveAssignment(RoleAssigneeType.USER, ASSIGNEE_ID, ROLE_ID, null))
                .thenReturn(true);

        assertThatThrownBy(() -> service.assignRole(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getHttpStatus())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void assignRole_workspaceScopedUserNotMember_throwsUnprocessableEntity() {
        AssignRoleCommand cmd = new AssignRoleCommand("USER", ASSIGNEE_ID, ROLE_ID, WORKSPACE_ID, null);
        when(roleRepository.findById(ROLE_ID)).thenReturn(Optional.of(activeRole()));
        when(workspaceMemberRepository.isActiveMember(WORKSPACE_ID, ASSIGNEE_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.assignRole(cmd))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> {
                    AppException appEx = (AppException) ex;
                    assertThat(appEx.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(appEx.getErrorCode()).isEqualTo(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_REQUIRES_WORKSPACE_MEMBER.code());
                });
    }

    @Test
    void activateAssignment_success_returnsActiveStatus() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(inactiveAssignment(id)));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = service.activateAssignment(id);

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    void deactivateAssignment_success_returnsInactiveStatus() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.of(activeAssignment(id)));
        when(assignmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleAssignmentResponse result = service.deactivateAssignment(id);

        assertThat(result.status()).isEqualTo("INACTIVE");
    }

    @Test
    void getAssignment_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(assignmentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAssignment(id))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getHttpStatus())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private IamRole activeRole() {
        return new IamRole(ROLE_ID, IamRoleCode.of("ADMIN"), "Admin", null, IamRoleStatus.ACTIVE,
                IamRoleScope.SYSTEM, IamRoleSource.SYSTEM_BUILT_IN,
                null, null, null, null, Instant.now(), Instant.now());
    }

    private IamRole inactiveRole() {
        return new IamRole(ROLE_ID, IamRoleCode.of("ADMIN"), "Admin", null, IamRoleStatus.INACTIVE,
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
