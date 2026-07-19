package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.common.exception.ValidationException;
import com.company.scopery.common.outbox.TransactionalOutboxService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.ConfirmPasswordResetCommand;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.platform.security.PasswordResetTokenService;
import com.company.scopery.platform.security.RefreshTokenService;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Component
public class ConfirmPasswordResetAction {

    private final IamUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenService resetTokenService;
    private final IamActivityLogger activityLogger;
    private final TransactionalOutboxService outboxService;
    private final ImmutableAuditEventService auditEventService;

    public ConfirmPasswordResetAction(IamUserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      RefreshTokenService refreshTokenService,
                                      PasswordResetTokenService resetTokenService,
                                      IamActivityLogger activityLogger,
                                      TransactionalOutboxService outboxService,
                                      ImmutableAuditEventService auditEventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.resetTokenService = resetTokenService;
        this.activityLogger = activityLogger;
        this.outboxService = outboxService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public void execute(ConfirmPasswordResetCommand command) {
        var userId = resetTokenService.consume(command.token())
                .orElseThrow(() -> new ValidationException("Password reset token is invalid or expired"));
        var user = userRepository.findById(userId).orElseThrow(IamExceptions::invalidCredentials);
        userRepository.save(user.withPassword(passwordEncoder.encode(command.newPassword())));
        refreshTokenService.revokeAll(user.id());

        outboxService.enqueue("IAM_USER", user.id(), "IAM_PASSWORD_RESET_COMPLETED", Map.of(
                "userId", user.id(),
                "occurredAt", Instant.now().toString(),
                "traceId", MDC.get("traceId") == null ? "" : MDC.get("traceId")));

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                IamActivityActions.CONFIRM_IAM_USER_PASSWORD_RESET,
                "Password reset confirmed for user: " + user.id());
        auditEventService.record(AuditEventType.IAM_PASSWORD_RESET_COMPLETED, user.id(), "USER",
                "IAM_USER", user.id(), null, null, null, Map.of("sessionsRevoked", true),
                "Password reset completed");
    }
}
