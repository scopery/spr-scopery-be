package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.LoginCommand;
import com.company.scopery.modules.iam.user.application.response.AuthResult;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import com.company.scopery.platform.security.JwtService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class LoginAction {

    private final IamUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final IamActivityLogger activityLogger;
    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public LoginAction(IamUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       IamActivityLogger activityLogger,
                       TransactionalOutboxService outboxService,
                       ImmutableAuditEventService auditEventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.activityLogger = activityLogger;
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public AuthResult execute(LoginCommand command) {
        Username usernameObj;
        try {
            usernameObj = Username.of(command.username());
        } catch (IllegalArgumentException e) {
            throw IamExceptions.invalidCredentials();
        }

        IamUser user = userRepository.findByUsername(usernameObj)
                .orElseThrow(IamExceptions::invalidCredentials);

        if (user.passwordHash() == null
                || !passwordEncoder.matches(command.password(), user.passwordHash())) {
            userRepository.save(user.recordFailedLogin());
            enqueueLoginFailed(user.id());
            throw IamExceptions.invalidCredentials();
        }

        // Do not leak account existence/status — same generic response as unknown user.
        if (user.status() != IamUserStatus.ACTIVE) {
            userRepository.save(user.recordFailedLogin());
            enqueueLoginFailed(user.id());
            throw IamExceptions.invalidCredentials();
        }

        IamUser loggedIn = userRepository.save(user.recordSuccessfulLogin());

        String accessToken = jwtService.generateToken(loggedIn.id(), loggedIn.username().value());
        String refreshToken = refreshTokenService.create(loggedIn.id());

        outboxService.enqueue("IAM_USER", loggedIn.id(), "IAM_USER_LOGGED_IN", Map.of(
                "userId", loggedIn.id(),
                "occurredAt", Instant.now().toString(),
                "traceId", MDC.get("traceId") == null ? "" : MDC.get("traceId")));

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, loggedIn.id(),
                IamActivityActions.LOGIN_IAM_USER, "User logged in: " + loggedIn.username().value());

        return new AuthResult(loggedIn, accessToken, refreshToken);
    }

    private void enqueueLoginFailed(UUID userId) {
        outboxService.enqueue("IAM_USER", userId, "IAM_LOGIN_FAILED", Map.of(
                "userId", userId,
                "occurredAt", Instant.now().toString(),
                "traceId", MDC.get("traceId") == null ? "" : MDC.get("traceId")));
        auditEventService.record(AuditEventType.IAM_LOGIN_FAILED, userId, "USER",
                "IAM_USER", userId, null, null, null, Map.of("outcome", "FAILED"), "Login failed");
    }
}
