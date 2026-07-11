package com.company.scopery.modules.iam.role.application.action;

import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
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
public class CreateWorkspaceIamRoleAction {

    private final IamRoleRepository iamRoleRepository;
    private final IamActivityLogger activityLogger;

    public CreateWorkspaceIamRoleAction(IamRoleRepository iamRoleRepository,
                                        IamActivityLogger activityLogger) {
        this.iamRoleRepository = iamRoleRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamRoleResponse execute(CreateIamRoleCommand command) {
        if (command.workspaceId() == null) {
            throw IamExceptions.roleWorkspaceScopeRequiresWorkspaceId();
        }

        IamRoleCode code = IamRoleCode.of(command.code());

        if (iamRoleRepository.existsByCodeAndWorkspaceId(code, command.workspaceId())) {
            throw IamExceptions.roleWorkspaceCodeAlreadyExists(code.value(), command.workspaceId());
        }

        UUID parentRoleId = null;
        if (command.parentRoleId() != null) {
            IamRole parent = iamRoleRepository.findById(command.parentRoleId())
                    .orElseThrow(() -> IamExceptions.roleParentNotFound(command.parentRoleId()));
            if (parent.roleSource() != IamRoleSource.SYSTEM_TEMPLATE) {
                throw IamExceptions.roleParentMustBeTemplate(parent.code().value());
            }
            parentRoleId = parent.id();
        }

        IamRole role = IamRole.createWorkspace(code, command.name(), command.description(),
                command.workspaceId(), parentRoleId);
        IamRole saved = iamRoleRepository.save(role);

        activityLogger.logSuccess(IamEntityTypes.IAM_ROLE, saved.id(),
                IamActivityActions.CREATE_WORKSPACE_ROLE,
                "Workspace role created: " + saved.code().value() + " in workspace " + command.workspaceId());

        return IamRoleResponse.from(saved);
    }
}
