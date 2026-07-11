package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.SuspendIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.application.service.IamUserQueryService;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamUserActionTest {

    @Mock private IamUserRepository userRepository;
    @Mock private IamActivityLogger activityLogger;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private IamSystemAuthorizationService systemAuthorizationService;

    private CreateIamUserAction createAction;
    private UpdateIamUserAction updateAction;
    private SuspendIamUserAction suspendAction;
    private IamUserQueryService queryService;

    private IamUser activeUser;
    private IamUser suspendedUser;

    @BeforeEach
    void setUp() {
        createAction = new CreateIamUserAction(userRepository, activityLogger, passwordEncoder);
        updateAction = new UpdateIamUserAction(userRepository, systemAuthorizationService, activityLogger);
        suspendAction = new SuspendIamUserAction(userRepository, systemAuthorizationService, activityLogger);
        queryService = new IamUserQueryService(userRepository, systemAuthorizationService);

        Instant now = Instant.now();
        activeUser = new IamUser(UUID.randomUUID(), Username.of("johndoe"),
                EmailAddress.of("john@example.com"), "John Doe",
                null, IamUserStatus.ACTIVE, now, now);
        suspendedUser = new IamUser(UUID.randomUUID(), Username.of("suspended.user"),
                EmailAddress.of("suspended@example.com"), "Suspended User",
                null, IamUserStatus.SUSPENDED, now, now);
    }

    @Test
    void createUser_newUser_succeeds() {
        when(userRepository.existsByUsername(Username.of("johndoe"))).thenReturn(false);
        when(userRepository.existsByEmail(EmailAddress.of("john@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashed-password");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamUserResponse response = createAction.execute(
                new CreateIamUserCommand("johndoe", "john@example.com", "John Doe", "Password@123"));

        assertThat(response.username()).isEqualTo("johndoe");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(userRepository).save(argThat(user -> "hashed-password".equals(user.passwordHash())));
    }

    @Test
    void createUser_duplicateUsername_throws409() {
        when(userRepository.existsByUsername(Username.of("johndoe"))).thenReturn(true);

        assertThatThrownBy(() -> createAction.execute(
                new CreateIamUserCommand("johndoe", "john@example.com", null, "Password@123")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_USERNAME_ALREADY_EXISTS.code()));
    }

    @Test
    void updateUser_suspendedUser_throws422() {
        when(userRepository.findById(suspendedUser.id())).thenReturn(Optional.of(suspendedUser));

        assertThatThrownBy(() -> updateAction.execute(
                new UpdateIamUserCommand(suspendedUser.id(), "New Name")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_SUSPENDED_CANNOT_BE_UPDATED.code()));
    }

    @Test
    void getUser_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryService.getUser(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_NOT_FOUND.code()));
    }

    @Test
    void suspendUser_activeUser_succeeds() {
        when(userRepository.findById(activeUser.id())).thenReturn(Optional.of(activeUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamUserResponse response = suspendAction.execute(new SuspendIamUserCommand(activeUser.id()));

        assertThat(response.status()).isEqualTo("SUSPENDED");
    }
}
