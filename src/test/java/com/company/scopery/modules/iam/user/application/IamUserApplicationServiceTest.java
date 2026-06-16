package com.company.scopery.modules.iam.user.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.EmailAddress;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IamUserApplicationServiceTest {

    @Mock
    private IamUserRepository userRepository;

    @Mock
    private IamActivityLogger activityLogger;

    private IamUserApplicationService service;

    private IamUser activeUser;
    private IamUser suspendedUser;

    @BeforeEach
    void setUp() {
        service = new IamUserApplicationService(userRepository, activityLogger);
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
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamUserResponse response = service.createUser(
                new CreateIamUserCommand("johndoe", "john@example.com", "John Doe"));

        assertThat(response.username()).isEqualTo("johndoe");
        assertThat(response.email()).isEqualTo("john@example.com");
        assertThat(response.status()).isEqualTo("ACTIVE");
        verify(userRepository).save(any());
    }

    @Test
    void createUser_duplicateUsername_throws409() {
        when(userRepository.existsByUsername(Username.of("johndoe"))).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(
                new CreateIamUserCommand("johndoe", "john@example.com", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_USERNAME_ALREADY_EXISTS.code()));
    }

    @Test
    void createUser_duplicateEmail_throws409() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(EmailAddress.of("john@example.com"))).thenReturn(true);

        assertThatThrownBy(() -> service.createUser(
                new CreateIamUserCommand("johndoe", "john@example.com", null)))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_EMAIL_ALREADY_EXISTS.code()));
    }

    @Test
    void updateUser_suspendedUser_throws422() {
        when(userRepository.findById(suspendedUser.id())).thenReturn(Optional.of(suspendedUser));

        assertThatThrownBy(() -> service.updateUser(
                new UpdateIamUserCommand(suspendedUser.id(), "New Name")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_SUSPENDED_CANNOT_BE_UPDATED.code()));
    }

    @Test
    void updateUser_activeUser_succeeds() {
        when(userRepository.findById(activeUser.id())).thenReturn(Optional.of(activeUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamUserResponse response = service.updateUser(new UpdateIamUserCommand(activeUser.id(), "New Name"));

        assertThat(response.fullName()).isEqualTo("New Name");
    }

    @Test
    void getUser_notFound_throws404() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUser(id))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.IAM_USER_NOT_FOUND.code()));
    }

    @Test
    void suspendUser_activeUser_succeeds() {
        when(userRepository.findById(activeUser.id())).thenReturn(Optional.of(activeUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        IamUserResponse response = service.suspendUser(activeUser.id());

        assertThat(response.status()).isEqualTo("SUSPENDED");
    }
}
