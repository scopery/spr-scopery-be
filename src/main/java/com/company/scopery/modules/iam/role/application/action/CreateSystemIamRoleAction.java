package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamErrorCatalog;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.shared.util.IamEnumParser;
import com.company.scopery.modules.iam.role.application.command.CreateIamRoleCommand;
import com.company.scopery.modules.iam.role.application.response.IamRoleResponse;
import com.company.scopery.modules.iam.role.domain.model.IamRole;
import com.company.scopery.modules.iam.role.domain.valueobject.IamRoleCode;
import com.company.scopery.modules.iam.role.domain.model.IamRoleRepository;
import com.company.scopery.modules.iam.role.domain.enums.IamRoleSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CreateSystemIamRoleAction {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;

    public CreateSystemIamRoleAction(IamRoleRepository iamRoleRepository,
                                     IamActivityLogger activityLogger) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleResponse execute(CreateIamRoleCommand command) {
        IamRoleCode code = IamRoleCode.of(command.code());

        if (iamRoleRepository.existsByCode(code)) {
            throw IamExceptions.roleCodeAlreadyExists(code.value());
        }

        IamRoleSource source = IamEnumParser.parseRequired(
                IamRoleSource.class, command.roleSource(),
                IamErrorCatalog.INVALID_IAM_ROLE_SOURCE.code(), "roleSource");

        UUID parentRoleId = resolveAndValidateParent(command.parentRoleId(), source);

        IamRole role = IamRole.createSystem(code, command.name(), command.description(), source, parentRoleId);
        IamRole saved = iamRoleRepository.save(role);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.CREATE_SYSTEM_ROLE, "System role created: " + saved.code().value());

        return IamRoleResponse.from(saved);
    }

    private UUID resolveAndValidateParent(UUID parentRoleId, IamRoleSource source) {
        if (parentRoleId == null) return null;
        IamRole parent = iamRoleRepository.findById(parentRoleId)
                .orElseThrow(() -> IamExceptions.roleParentNotFound(parentRoleId));
        if (parent.roleSource() != IamRoleSource.SYSTEM_TEMPLATE) {
            throw IamExceptions.roleParentMustBeTemplate(parent.code().value());
        }
        return parent.id();
    }
}
