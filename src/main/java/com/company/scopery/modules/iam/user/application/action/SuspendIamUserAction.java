package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.application.command.SuspendIamUserCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class SuspendIamUserAction {

    private final IamUserRepository iamUserRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public SuspendIamUserAction(IamUserRepository iamUserRepository,
                                IamSystemAuthorizationService systemAuthorizationService,
                                IamActivityLogger activityLogger) {
        this.iamUserRepository = iamUserRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamUserResponse execute(SuspendIamUserCommand command) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_USER.legacyRightCode());
        IamUser saved = iamUserRepository.save(iamUserRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamUserNotFound(command.id())).suspend());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.SUSPEND_IAM_USER, "User suspended: " + saved.username().value());
        return IamUserResponse.from(saved);
    }
}
