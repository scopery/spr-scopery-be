package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.constant.ApiPaths;
import com.company.scopery.common.exception.AppException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.eventregistry.eventdefinition.domain.model.EventDefinitionRepository;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.user.application.command.ChangePasswordCommand;
import com.company.scopery.modules.iam.user.application.command.ConfirmPasswordResetCommand;
import com.company.scopery.modules.iam.user.application.command.LogoutCommand;
import com.company.scopery.modules.iam.user.application.command.RefreshTokenCommand;
import com.company.scopery.modules.iam.user.application.command.RequestPasswordResetCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.notification.shared.NotificationProperties;
import com.company.scopery.platform.security.CookieUtil;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.PasswordResetTokenService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Phase02AuthFlowActionTest {

    @Mock private IamUserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordResetTokenService resetTokenService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private IamActivityLogger activityLogger;
    @Mock private CurrentUserAuthorizationService currentUserService;
    @Mock private TransactionalOutboxService outboxService;
    @Mock private EmailNotificationTriggerPublisher notificationPublisher;
    @Mock private EventDefinitionRepository eventDefinitionRepository;
    @Mock private NotificationProperties notificationProperties;
    @Mock private ImmutableAuditEventService auditEventService;

    private RefreshTokenAction refreshTokenAction;
    private LogoutAction logoutAction;
    private ChangePasswordAction changePasswordAction;
    private RequestPasswordResetAction requestPasswordResetAction;
    private ConfirmPasswordResetAction confirmPasswordResetAction;

    private IamUser user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        refreshTokenAction = new RefreshTokenAction(userRepository, jwtService, refreshTokenService, activityLogger);
        logoutAction = new LogoutAction(refreshTokenService, activityLogger);
        changePasswordAction = new ChangePasswordAction(
                currentUserService, userRepository, passwordEncoder, refreshTokenService, activityLogger, auditEventService);
        requestPasswordResetAction = new RequestPasswordResetAction(
                userRepository, resetTokenService, activityLogger, outboxService,
                notificationPublisher, eventDefinitionRepository, notificationProperties);
        confirmPasswordResetAction = new ConfirmPasswordResetAction(
                userRepository, passwordEncoder, refreshTokenService, resetTokenService, activityLogger, outboxService, auditEventService);

        userId = UUID.randomUUID();
        user = IamUser.of(userId, Username.of("alice"), EmailAddress.of("alice@example.com"),
                "Alice", "old-hash", IamUserStatus.ACTIVE, Instant.now(), Instant.now());
    }

    @Test
    void refresh_valid_rotatesToken() {
        when(refreshTokenService.validate("old-refresh")).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenService.create(userId)).thenReturn("new-refresh");
        when(jwtService.generateToken(userId, "alice")).thenReturn("new-access");

        AuthResult result = refreshTokenAction.execute(new RefreshTokenCommand("old-refresh"));

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        verify(refreshTokenService).revoke("old-refresh");
        verify(refreshTokenService).create(userId);
    }

    @Test
    void refresh_revoked_rejected() {
        when(refreshTokenService.validate("revoked-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenAction.execute(new RefreshTokenCommand("revoked-token")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.INVALID_CREDENTIALS.code()));
        verify(refreshTokenService, never()).create(any());
    }

    @Test
    void refresh_expired_rejected() {
        when(refreshTokenService.validate("expired-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenAction.execute(new RefreshTokenCommand("expired-token")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.INVALID_CREDENTIALS.code()));
        verify(jwtService, never()).generateToken(any(), anyString());
    }

    @Test
    void logout_valid_revokesTokenAndClearsCookie() {
        when(refreshTokenService.validate("refresh-xyz")).thenReturn(Optional.of(userId));

        logoutAction.execute(new LogoutCommand("refresh-xyz"));

        verify(refreshTokenService).revoke("refresh-xyz");

        ResponseCookie accessClear = CookieUtil.clearAccessCookie(true);
        ResponseCookie refreshClear = CookieUtil.clearRefreshCookie(true);
        assertThat(accessClear.getName()).isEqualTo(CookieUtil.ACCESS_TOKEN_COOKIE);
        assertThat(accessClear.getMaxAge().getSeconds()).isZero();
        assertThat(accessClear.getPath()).isEqualTo("/");
        assertThat(refreshClear.getName()).isEqualTo(CookieUtil.REFRESH_TOKEN_COOKIE);
        assertThat(refreshClear.getMaxAge().getSeconds()).isZero();
        assertThat(refreshClear.getPath()).isEqualTo(ApiPaths.IAM_AUTH);
    }

    @Test
    void changePassword_wrongCurrentPassword_rejected() {
        when(currentUserService.resolveCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        assertThatThrownBy(() -> changePasswordAction.execute(
                new ChangePasswordCommand("wrong", "NewPass@123")))
                .isInstanceOf(AppException.class)
                .satisfies(e -> assertThat(((AppException) e).getErrorCode())
                        .isEqualTo(IamErrorCatalog.INVALID_CREDENTIALS.code()));
        verify(refreshTokenService, never()).revokeAll(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_valid_revokesSessions() {
        when(currentUserService.resolveCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("OldPass@123", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-hash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        changePasswordAction.execute(new ChangePasswordCommand("OldPass@123", "NewPass@123"));

        verify(userRepository).save(any(IamUser.class));
        verify(refreshTokenService).revokeAll(userId);
    }

    @Test
    void resetRequest_unknownEmail_outwardSuccess() {
        when(userRepository.findByEmail(EmailAddress.of("unknown@example.com"))).thenReturn(Optional.empty());

        assertThatCode(() -> requestPasswordResetAction.execute(
                new RequestPasswordResetCommand("unknown@example.com")))
                .doesNotThrowAnyException();

        verify(resetTokenService, never()).create(any());
        verify(outboxService, never()).enqueue(anyString(), any(), anyString(), any());
        verify(notificationPublisher, never()).publish(any());
    }

    @Test
    void resetConfirm_valid_updatesPasswordAndRevokesSessions() {
        when(resetTokenService.consume("raw-token")).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPass@123")).thenReturn("new-hash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        confirmPasswordResetAction.execute(new ConfirmPasswordResetCommand("raw-token", "NewPass@123"));

        verify(userRepository).save(any(IamUser.class));
        verify(refreshTokenService).revokeAll(userId);
        verify(outboxService).enqueue(eq("IAM_USER"), eq(userId), eq("IAM_PASSWORD_RESET_COMPLETED"), any());
    }
}
