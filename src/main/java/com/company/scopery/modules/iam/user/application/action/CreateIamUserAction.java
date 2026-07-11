package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.CreateIamUserCommand;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.valueobject.EmailAddress;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.valueobject.Username;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateIamUserAction {

    private final IamUserRepository iamUserRepository;
    private final IamActivityLogger activityLogger;
    private final PasswordEncoder passwordEncoder;

    public CreateIamUserAction(IamUserRepository iamUserRepository,
                               IamActivityLogger activityLogger,
                               PasswordEncoder passwordEncoder) {
        this.iamUserRepository = iamUserRepository;
        this.activityLogger = activityLogger;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public IamUserResponse execute(CreateIamUserCommand command) {
        Username username = Username.of(command.username());
        EmailAddress email = EmailAddress.of(command.email());

        if (iamUserRepository.existsByUsername(username)) {
            throw IamExceptions.usernameAlreadyExists(username.value());
        }
        if (iamUserRepository.existsByEmail(email)) {
            throw IamExceptions.emailAlreadyExists(email.value());
        }

        IamUser user = IamUser.create(username, email, command.fullName())
                .withPassword(passwordEncoder.encode(command.password()));
        IamUser saved = iamUserRepository.save(user);

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.CREATE_IAM_USER, "User created: " + saved.username().value());

        return IamUserResponse.from(saved);
    }
}
