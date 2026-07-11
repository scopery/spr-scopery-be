package com.company.scopery.modules.iam.user.application.action;

import com.company.scopery.modules.iam.authorization.application.service.IamSystemAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.application.command.UpdateIamUserCommand;
import com.company.scopery.modules.iam.user.application.response.IamUserResponse;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdateIamUserAction {

    private final IamUserRepository iamUserRepository;
    private final IamSystemAuthorizationService systemAuthorizationService;
    private final IamActivityLogger activityLogger;

    public UpdateIamUserAction(IamUserRepository iamUserRepository,
                               IamSystemAuthorizationService systemAuthorizationService,
                               IamActivityLogger activityLogger) {
        this.iamUserRepository = iamUserRepository;
        this.systemAuthorizationService = systemAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamUserResponse execute(UpdateIamUserCommand command) {
        systemAuthorizationService.requireSystemRight(IamAuthorities.SYSTEM_IAM_MANAGE_USER.legacyRightCode());
        IamUser user = iamUserRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamUserNotFound(command.id()));
        if (user.status() == IamUserStatus.SUSPENDED) {
            throw IamExceptions.iamUserSuspendedCannotBeUpdated(user.username().value());
        }
        IamUser saved = iamUserRepository.save(user.update(command.fullName()));

        activityLogger.logSuccess(IamEntityTypes.IAM_USER, saved.id(),
                IamActivityActions.UPDATE_IAM_USER, "User updated: " + saved.username().value());

        return IamUserResponse.from(saved);
    }
}
