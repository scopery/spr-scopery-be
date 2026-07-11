package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.application.command.DeactivateIamRoleCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class DeactivateIamRoleAction {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;

    public DeactivateIamRoleAction(IamRoleRepository iamRoleRepository,
                                   IamActivityLogger activityLogger) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleResponse execute(DeactivateIamRoleCommand command) {
        IamRole role = iamRoleRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(command.id()));
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        IamRole saved = iamRoleRepository.save(role.deactivate());
        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.DEACTIVATE_IAM_ROLE, "Role deactivated: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }
}
