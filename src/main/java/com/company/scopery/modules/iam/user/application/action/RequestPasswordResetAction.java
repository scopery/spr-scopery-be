package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.user.application.command.RequestPasswordResetCommand;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.platform.security.PasswordResetTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RequestPasswordResetAction {

    private static final Logger log = LoggerFactory.getLogger(RequestPasswordResetAction.class);

    private final IamUserRepository userRepository;
    private final PasswordResetTokenService resetTokenService;
    private final IamActivityLogger activityLogger;

    public RequestPasswordResetAction(IamUserRepository userRepository,
                                      PasswordResetTokenService resetTokenService,
                                      IamActivityLogger activityLogger) {
        this.userRepository = userRepository;
        this.resetTokenService = resetTokenService;
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public void execute(RequestPasswordResetCommand command) {
        try {
            userRepository.findByEmail(EmailAddress.of(command.email())).ifPresent(user -> {
                resetTokenService.create(user.id());
                log.info("Password reset requested for userId={}", user.id());
                activityLogger.logSuccess(IamEntityTypes.IAM_USER, user.id(),
                        IamActivityActions.REQUEST_IAM_USER_PASSWORD_RESET,
                        "Password reset requested for user: " + user.id());
            });
        } catch (IllegalArgumentException ignored) { }
    }
}
