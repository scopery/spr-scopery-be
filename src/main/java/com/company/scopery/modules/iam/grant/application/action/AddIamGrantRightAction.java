package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.application.command.AddIamGrantRightCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantRightResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRight;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
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
public class AddIamGrantRightAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamRightRepository rightRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final AuthorizationDecisionService authorizationDecisionService;
    private final IamActivityLogger activityLogger;

    public AddIamGrantRightAction(IamAccessGrantRepository grantRepository,
                                  IamAccessGrantRightRepository grantRightRepository,
                                  IamRightRepository rightRepository,
                                  IamAuthResourceRepository resourceRepository,
                                  CurrentUserAuthorizationService currentUserAuthorizationService,
                                  AuthorizationDecisionService authorizationDecisionService,
                                  IamActivityLogger activityLogger) {
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.rightRepository = rightRepository;
        this.resourceRepository = resourceRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.authorizationDecisionService = authorizationDecisionService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public IamAccessGrantRightResponse execute(AddIamGrantRightCommand command) {
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

        IamRight right = rightRepository.findById(command.rightId())
                .orElseThrow(() -> IamExceptions.iamRightNotFound(command.rightId()));

        if (right.status() != IamRightStatus.ACTIVE) {
            throw IamExceptions.rightInactiveCannotBeUsed(right.code().value());
        }

        if (grantRightRepository.existsByGrantIdAndRightId(command.grantId(), command.rightId())) {
            throw IamExceptions.iamAccessGrantRightAlreadyExists(command.grantId(), command.rightId());
        }

        IamAccessGrantRight grantRight = IamAccessGrantRight.create(command.grantId(), command.rightId());
        IamAccessGrantRight saved = grantRightRepository.save(grantRight);

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, grant.id(),
                IamActivityActions.ADD_IAM_GRANT_RIGHT,
                "Right " + command.rightId() + " added to grant " + command.grantId());

        return IamAccessGrantRightResponse.from(saved);
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
