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
import com.company.scopery.modules.iam.user.application.command.DeactivateIamUserCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DeactivateIamUserAction {

    private final IamUserRepository iamUserRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public DeactivateIamUserAction(IamUserRepository iamUserRepository,
                                   IamSystemAuthorizationService systemAuthorizationService,
                                   IamActivityLogger activityLogger) {
        this.iamUserRepository = iamUserRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamUserResponse execute(DeactivateIamUserCommand command) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_USER.legacyRightCode());
        IamUser saved = iamUserRepository.save(iamUserRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamUserNotFound(command.id())).deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_USER, "User deactivated: " + saved.username().value());
        return IamUserResponse.from(saved);
    }
}
