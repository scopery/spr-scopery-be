package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.ChangePasswordCommand;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.platform.security.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class ChangePasswordAction {

    private final CurrentUserAuthorizationService currentUserService;
    private final IamUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final IamActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public ChangePasswordAction(CurrentUserAuthorizationService currentUserService,
                                IamUserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                RefreshTokenService refreshTokenService,
                                IamActivityLogger activityLogger,
                                ImmutableAuditEventService auditEventService) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public void execute(ChangePasswordCommand command) {
        var user = currentUserService.resolveCurrentUser();
        if (user.passwordHash() == null || !passwordEncoder.matches(command.currentPassword(), user.passwordHash())) {
            throw IamExceptions.invalidCredentials();
        }
        userRepository.save(user.withPassword(passwordEncoder.encode(command.newPassword())));
        refreshTokenService.revokeAll(user.id());

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                IamActivityActions.CHANGE_IAM_USER_PASSWORD, "Password changed for user: " + user.username().value());
        auditEventService.record(AuditEventType.IAM_PASSWORD_CHANGED, user.id(), "USER",
                "IAM_USER", user.id(), null, null, null, Map.of("sessionsRevoked", true),
                "Password changed");
    }
}
