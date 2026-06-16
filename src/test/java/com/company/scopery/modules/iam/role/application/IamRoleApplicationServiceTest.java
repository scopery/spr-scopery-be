package com.company.scopery.modules.iam.role.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.UpdateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.IamRole;
import com.company.scopery.modules.iam.role.domain.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.IamRoleStatus;
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
class IamRoleApplicationServiceTest {

    @Mock private IamRoleRepository roleRepository;
    @Mock private IamActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;

    private IamRoleApplicationService service;
    private IamRole activeSystemRole;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        service = new IamRoleApplicationService(roleRepository, activityLogger, currentUserService);
        Instant now = Instant.now();
        activeSystemRole = systemRole(UUID.randomUUID(), "ADMIN", IamRoleSource.SYSTEM_BUILT_IN);
        currentUser = new IamUser(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    // ── createSystemRole ─────────────────────────────────────────────────────

    @Test
    void createSystemRole_success_returnsActiveRole() {
        when(roleRepository.existsByCode(IamRoleCode.of("ADMIN"))).thenReturn(false);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.createSystemRole(
                new CreateIamRoleCommand("ADMIN", "Administrator", "Full access",
                        "SYSTEM", "SYSTEM_BUILT_IN", null, null));

        assertThat(response.code()).isEqualTo("ADMIN");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.roleScope()).isEqualTo("SYSTEM");
        assertThat(response.isSystem()).isTrue();
    }

    @Test
    void createSystemRole_duplicateCode_throws409() {
        when(roleRepository.existsByCode(IamRoleCode.of("ADMIN"))).thenReturn(true);

        assertThatThrownBy(() -> service.createSystemRole(
                new CreateIamRoleCommand("ADMIN", "Administrator", null,
                        "SYSTEM", "SYSTEM_BUILT_IN", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_CODE_ALREADY_EXISTS.code()));
    }

    // ── createWorkspaceRole ──────────────────────────────────────────────────

    @Test
    void createWorkspaceRole_success_returnsWorkspaceRole() {
        UUID workspaceId = UUID.randomUUID();
        when(roleRepository.existsByCodeAndWorkspaceId(IamRoleCode.of("EDITOR"), workspaceId)).thenReturn(false);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.createWorkspaceRole(
                new CreateIamRoleCommand("EDITOR", "Editor", "Can edit documents",
                        null, null, workspaceId, null));

        assertThat(response.code()).isEqualTo("EDITOR");
        assertThat(response.roleScope()).isEqualTo("WORKSPACE");
        assertThat(response.workspaceId()).isEqualTo(workspaceId);
        assertThat(response.isSystem()).isFalse();
    }

    @Test
    void createWorkspaceRole_missingWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> service.createWorkspaceRole(
                new CreateIamRoleCommand("EDITOR", "Editor", null,
                        null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            IamErrorCatalog.IAM_ROLE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID.code());
                });
    }

    @Test
    void createWorkspaceRole_duplicateCodeInWorkspace_throws409() {
        UUID workspaceId = UUID.randomUUID();
        when(roleRepository.existsByCodeAndWorkspaceId(IamRoleCode.of("EDITOR"), workspaceId)).thenReturn(true);

        assertThatThrownBy(() -> service.createWorkspaceRole(
                new CreateIamRoleCommand("EDITOR", "Editor", null,
                        null, null, workspaceId, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_WORKSPACE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void createWorkspaceRole_withParentTemplate_succeeds() {
        UUID workspaceId = UUID.randomUUID();
        IamRole templateParent = systemRole(UUID.randomUUID(), "SYSTEM_GOVERNANCE_ADMIN", IamRoleSource.SYSTEM_TEMPLATE);

        when(roleRepository.findById(templateParent.id())).thenReturn(Optional.of(templateParent));
        when(roleRepository.existsByCodeAndWorkspaceId(IamRoleCode.of("WS_ADMIN"), workspaceId)).thenReturn(false);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.createWorkspaceRole(
                new CreateIamRoleCommand("WS_ADMIN", "Workspace Admin", null,
                        null, null, workspaceId, templateParent.id()));

        assertThat(response.parentRoleId()).isEqualTo(templateParent.id());
    }

    @Test
    void createWorkspaceRole_parentIsBuiltIn_throws422() {
        UUID workspaceId = UUID.randomUUID();
        IamRole builtInParent = systemRole(UUID.randomUUID(), "SUPER_ADMIN", IamRoleSource.SYSTEM_BUILT_IN);

        when(roleRepository.findById(builtInParent.id())).thenReturn(Optional.of(builtInParent));
        when(roleRepository.existsByCodeAndWorkspaceId(IamRoleCode.of("WS_ADMIN"), workspaceId)).thenReturn(false);

        assertThatThrownBy(() -> service.createWorkspaceRole(
                new CreateIamRoleCommand("WS_ADMIN", "Workspace Admin", null,
                        null, null, workspaceId, builtInParent.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            IamErrorCatalog.IAM_ROLE_PARENT_MUST_BE_TEMPLATE.code());
                });
    }

    // ── updateRole ───────────────────────────────────────────────────────────

    @Test
    void updateRole_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateRole(new UpdateIamRoleCommand(id, "New Name", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_NOT_FOUND.code()));
    }

    @Test
    void updateRole_deletedRole_throws422() {
        IamRole deleted = activeSystemRole.softDelete(UUID.randomUUID());
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> service.updateRole(
                new UpdateIamRoleCommand(activeSystemRole.id(), "New Name", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_DELETED_CANNOT_BE_MODIFIED.code()));
    }

    // ── softDeleteRole ───────────────────────────────────────────────────────

    @Test
    void softDeleteRole_systemBuiltIn_throws422() {
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(activeSystemRole));

        assertThatThrownBy(() -> service.softDeleteRole(activeSystemRole.id()))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(ae.getErrorCode()).isEqualTo(
                            IamErrorCatalog.IAM_ROLE_SYSTEM_BUILTIN_CANNOT_BE_DELETED.code());
                });
    }

    @Test
    void softDeleteRole_workspaceRole_succeeds() {
        UUID workspaceId = UUID.randomUUID();
        IamRole wsRole = IamRole.createWorkspace(
                IamRoleCode.of("EDITOR"), "Editor", null, workspaceId, null);
        when(roleRepository.findById(wsRole.id())).thenReturn(Optional.of(wsRole));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.softDeleteRole(wsRole.id());

        assertThat(response.status()).isEqualTo("DELETED");
        assertThat(response.deletedAt()).isNotNull();
    }

    // ── activate/deactivate ──────────────────────────────────────────────────

    @Test
    void deactivateRole_activeRole_returnsInactive() {
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(activeSystemRole));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.deactivateRole(activeSystemRole.id());

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void activateRole_inactiveRole_returnsActive() {
        IamRole inactive = activeSystemRole.deactivate();
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(inactive));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = service.activateRole(activeSystemRole.id());

        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private IamRole systemRole(UUID id, String code, IamRoleSource source) {
        Instant now = Instant.now();
        return new IamRole(id, IamRoleCode.of(code), code + " role", "Description",
                IamRoleStatus.ACTIVE, IamRoleScope.SYSTEM, source,
                null, null, null, null, now, now);
    }
}
