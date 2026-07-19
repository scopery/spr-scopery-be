package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginActionTest {

    @Mock private IamUserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private IamActivityLogger activityLogger;
    @Mock private TransactionalOutboxService outboxService;
    @Mock private ImmutableAuditEventService auditEventService;

    private LoginAction loginAction;

    @BeforeEach
    void setUp() {
        loginAction = new LoginAction(userRepository, passwordEncoder, jwtService,
                refreshTokenService, activityLogger, outboxService, auditEventService);
    }

    @Test
    void login_inactiveUser_returnsSameGenericInvalidCredentials() {
        IamUser inactive = IamUser.of(UUID.randomUUID(), Username.of("inactive"),
                EmailAddress.of("i@example.com"), "Inactive", "hash",
                IamUserStatus.INACTIVE, Instant.now(), Instant.now());
        when(userRepository.findByUsername(Username.of("inactive"))).thenReturn(Optional.of(inactive));
        when(passwordEncoder.matches("Password@123", "hash")).thenReturn(true);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> loginAction.execute(new LoginCommand("inactive", "Password@123")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.INVALID_CREDENTIALS.code()));

        verify(jwtService, never()).generateToken(any(), anyString());
        verify(outboxService).enqueue(eq("IAM_USER"), eq(inactive.id()), eq("IAM_LOGIN_FAILED"), any());
    }

    @Test
    void login_activeUser_emitsLoggedInEvent() {
        IamUser active = IamUser.of(UUID.randomUUID(), Username.of("active"),
                EmailAddress.of("a@example.com"), "Active", "hash",
                IamUserStatus.ACTIVE, Instant.now(), Instant.now());
        when(userRepository.findByUsername(Username.of("active"))).thenReturn(Optional.of(active));
        when(passwordEncoder.matches("Password@123", "hash")).thenReturn(true);
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(), anyString())).thenReturn("access");
        when(refreshTokenService.create(any())).thenReturn("refresh");

        loginAction.execute(new LoginCommand("active", "Password@123"));

        verify(outboxService).enqueue(eq("IAM_USER"), eq(active.id()), eq("IAM_USER_LOGGED_IN"), any());
    }
}
