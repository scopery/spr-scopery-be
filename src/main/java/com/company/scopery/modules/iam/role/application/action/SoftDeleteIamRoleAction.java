package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import com.company.scopery.modules.iam.role.application.command.SoftDeleteIamRoleCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class SoftDeleteIamRoleAction {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;

    public SoftDeleteIamRoleAction(IamRoleRepository iamRoleRepository,
                                   IamActivityLogger activityLogger,
                                   CurrentUserAuthorizationService currentUserService) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public IamRoleResponse execute(SoftDeleteIamRoleCommand command) {
        IamRole role = iamRoleRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(command.id()));
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        if (role.roleSource() == IamRoleSource.SYSTEM_BUILT_IN) {
            throw IamExceptions.roleSystemBuiltInCannotBeDeleted(role.code().value());
        }
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamRole saved = iamRoleRepository.save(role.softDelete(actorId));

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.SOFT_DELETE_IAM_ROLE, "Role soft-deleted: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }
}
