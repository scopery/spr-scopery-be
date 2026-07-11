package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.application.command.RemoveIamGrantPermissionActionCommand;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveIamGrantPermissionActionAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final AuthorizationDecisionService authorizationDecisionService;
    private final IamActivityLogger activityLogger;

    public RemoveIamGrantPermissionActionAction(
            IamAccessGrantRepository grantRepository,
            IamAccessGrantPermissionActionRepository grantActionRepository,
            IamPermissionActionDefinitionRepository actionRepository,
            IamAuthResourceRepository resourceRepository,
            CurrentUserAuthorizationService currentUserAuthorizationService,
            AuthorizationDecisionService authorizationDecisionService,
            IamActivityLogger activityLogger) {
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.actionRepository = actionRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.authorizationDecisionService = authorizationDecisionService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public void execute(RemoveIamGrantPermissionActionCommand command) {
        IamAccessGrant grant = grantRepository.findById(command.grantId())
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(command.grantId()));
        if (grant.status() == IamAccessGrantStatus.REVOKED) {
            throw IamExceptions.iamAccessGrantRevokedCannotBeModified(grant.id());
        }

        IamAuthResource resource = resourceRepository.findById(grant.resourceId())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(grant.resourceId()));
        UUID actorId = currentUserAuthorizationService.resolveCurrentUser().id();
        authorizationDecisionService.requireAccess(
                new AuthorizationRequest(actorId, resource.id(), manageAuthorityFor(resource)));

        IamPermissionActionDefinition action = actionRepository.findById(command.permissionActionId())
                .orElseThrow(() -> IamExceptions.iamPermissionActionNotFound(command.permissionActionId()));
        if (!grantActionRepository.existsByGrantIdAndPermissionActionId(command.grantId(), action.id())) {
            throw IamExceptions.iamAccessGrantPermissionActionNotFound(command.grantId(), action.id());
        }

        grantActionRepository.deleteByGrantIdAndPermissionActionId(command.grantId(), action.id());

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, grant.id(),
                IamActivityActions.REMOVE_IAM_GRANT_PERMISSION_ACTION,
                "Permission action " + action.id() + " removed from grant " + grant.id());
    }

    private IamPermissionAction manageAuthorityFor(IamAuthResource resource) {
        return switch (resource.resourceType()) {
            case ORGANIZATION -> IamAuthorities.ORGANIZATION_MANAGE;
            case WORKSPACE -> IamAuthorities.WORKSPACE_ACCESS_MANAGE_ACCESS;
            case TEAM -> IamAuthorities.TEAM_MANAGE;
            default -> IamAuthorities.SYSTEM_IAM_MANAGE_ACCESS_GRANT;
        };
    }
}
