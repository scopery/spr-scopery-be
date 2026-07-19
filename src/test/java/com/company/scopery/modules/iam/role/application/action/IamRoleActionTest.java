package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.role.application.command.ActivateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.DeactivateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.SoftDeleteIamRoleCommand;
import com.company.scopery.modules.iam.role.application.command.UpdateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleScope;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleStatus;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
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
class IamRoleActionTest {

    @Mock private IamRoleRepository roleRepository;
    @Mock private IamActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;

    private CreateSystemIamRoleAction createSystemAction;
    private CreateWorkspaceIamRoleAction createWorkspaceAction;
    private UpdateIamRoleAction updateAction;
    private SoftDeleteIamRoleAction softDeleteAction;
    private ActivateIamRoleAction activateAction;
    private DeactivateIamRoleAction deactivateAction;

    private IamRole activeSystemRole;
    private IamUser currentUser;

    @BeforeEach
    void setUp() {
        createSystemAction = new CreateSystemIamRoleAction(roleRepository, activityLogger);
        createWorkspaceAction = new CreateWorkspaceIamRoleAction(roleRepository, activityLogger);
        updateAction = new UpdateIamRoleAction(roleRepository, activityLogger);
        softDeleteAction = new SoftDeleteIamRoleAction(roleRepository, activityLogger, currentUserService);
        activateAction = new ActivateIamRoleAction(roleRepository, activityLogger);
        deactivateAction = new DeactivateIamRoleAction(roleRepository, activityLogger);

        Instant now = Instant.now();
        activeSystemRole = new IamRole(UUID.randomUUID(), IamRoleCode.of("ADMIN"), "Administrator", "Description",
                IamRoleStatus.ACTIVE, IamRoleScope.SYSTEM, IamRoleSource.SYSTEM_BUILT_IN,
                null, null, null, null, now, now);
        currentUser = IamUser.of(UUID.randomUUID(), Username.of("admin"),
                EmailAddress.of("admin@example.com"), "Admin", null, IamUserStatus.ACTIVE, now, now);
        lenient().when(currentUserService.resolveCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createSystemRole_success_returnsActiveRole() {
        when(roleRepository.existsByCode(IamRoleCode.of("ADMIN"))).thenReturn(false);
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = createSystemAction.execute(
                new CreateIamRoleCommand("ADMIN", "Administrator", "Full access",
                        "SYSTEM", "SYSTEM_BUILT_IN", null, null));

        assertThat(response.code()).isEqualTo("ADMIN");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.isSystem()).isTrue();
    }

    @Test
    void createSystemRole_duplicateCode_throws409() {
        when(roleRepository.existsByCode(IamRoleCode.of("ADMIN"))).thenReturn(true);

        assertThatThrownBy(() -> createSystemAction.execute(
                new CreateIamRoleCommand("ADMIN", "Administrator", null,
                        "SYSTEM", "SYSTEM_BUILT_IN", null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_CODE_ALREADY_EXISTS.code()));
    }

    @Test
    void createWorkspaceRole_missingWorkspaceId_throwsBadRequest() {
        assertThatThrownBy(() -> createWorkspaceAction.execute(
                new CreateIamRoleCommand("EDITOR", "Editor", null, null, null, null, null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ae = (AppException) e;
                    assertThat(ae.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                });
    }

    @Test
    void softDeleteRole_systemBuiltIn_throws422() {
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(activeSystemRole));

        assertThatThrownBy(() -> softDeleteAction.execute(new SoftDeleteIamRoleCommand(activeSystemRole.id())))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_SYSTEM_BUILTIN_CANNOT_BE_DELETED.code()));
    }

    @Test
    void softDeleteRole_workspaceRole_succeeds() {
        UUID workspaceId = UUID.randomUUID();
        IamRole wsRole = IamRole.createWorkspace(IamRoleCode.of("EDITOR"), "Editor", null, workspaceId, null);
        when(roleRepository.findById(wsRole.id())).thenReturn(Optional.of(wsRole));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = softDeleteAction.execute(new SoftDeleteIamRoleCommand(wsRole.id()));

        assertThat(response.status()).isEqualTo("DELETED");
        assertThat(response.deletedAt()).isNotNull();
    }

    @Test
    void deactivateRole_activeRole_returnsInactive() {
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(activeSystemRole));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = deactivateAction.execute(new DeactivateIamRoleCommand(activeSystemRole.id()));

        assertThat(response.status()).isEqualTo("INACTIVE");
    }

    @Test
    void activateRole_inactiveRole_returnsActive() {
        IamRole inactive = activeSystemRole.deactivate();
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(inactive));
        when(roleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamRoleResponse response = activateAction.execute(new ActivateIamRoleCommand(activeSystemRole.id()));

        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    @Test
    void updateRole_deletedRole_throws422() {
        IamRole deleted = activeSystemRole.softDelete(UUID.randomUUID());
        when(roleRepository.findById(activeSystemRole.id())).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> updateAction.execute(
                new UpdateIamRoleCommand(activeSystemRole.id(), "New Name", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_ROLE_DELETED_CANNOT_BE_MODIFIED.code()));
    }
}
