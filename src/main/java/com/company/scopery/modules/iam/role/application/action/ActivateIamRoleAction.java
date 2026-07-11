package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.application.command.ActivateIamRoleCommand;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ActivateIamRoleAction {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;

    public ActivateIamRoleAction(IamRoleRepository iamRoleRepository,
                                 IamActivityLogger activityLogger) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleResponse execute(ActivateIamRoleCommand command) {
        IamRole role = iamRoleRepository.findById(command.id())
                .orElseThrow(() -> IamExceptions.iamRoleNotFound(command.id()));
        if (role.isDeleted()) {
            throw IamExceptions.roleDeletedCannotBeModified(role.id());
        }
        IamRole saved = iamRoleRepository.save(role.activate());
        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.ACTIVATE_IAM_ROLE, "Role activated: " + saved.code().value());
        return IamRoleResponse.from(saved);
    }
}
