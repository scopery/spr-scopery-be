package com.company.scopery.modules.iam.authorization.application.service;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.shared.constant.IamActionCodes;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorizationGuard {

    private final CurrentUserAuthorizationService currentUserService;
    private final AuthorizationDecisionService decisionService;
    private final IamAuthResourceRepository resourceRepository;

    public AuthorizationGuard(CurrentUserAuthorizationService currentUserService,
                               AuthorizationDecisionService decisionService,
                               IamAuthResourceRepository resourceRepository) {
        this.currentUserService = currentUserService;
        this.decisionService = decisionService;
        this.resourceRepository = resourceRepository;
    }

    public void requireView(UUID resourceId) {
        require(resourceId, IamActionCodes.VIEW);
    }

    public void requireCreate(UUID resourceId) {
        require(resourceId, IamActionCodes.CREATE);
    }

    public void requireUpdate(UUID resourceId) {
        require(resourceId, IamActionCodes.UPDATE);
    }

    public void requireDelete(UUID resourceId) {
        require(resourceId, IamActionCodes.DELETE);
    }

    public void requireManagePermission(UUID resourceId) {
        require(resourceId, IamActionCodes.MANAGE_PERMISSION);
    }

    public void require(UUID resourceId, String rightCode) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        decisionService.requireAccess(new AuthorizationRequest(userId, resourceId, rightCode));
    }

    public void require(UUID resourceId, IamPermissionAction authority) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        decisionService.requireAccess(new AuthorizationRequest(userId, resourceId, authority));
    }

    public void requireByRef(IamResourceType resourceType, UUID refId, IamPermissionAction authority) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        requireByRef(userId, resourceType, refId, authority);
    }

    public void requireByRef(UUID userId, IamResourceType resourceType, UUID refId, IamPermissionAction authority) {
        UUID resourceId = resourceRepository.findByRefIdAndResourceType(refId, resourceType)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(refId))
                .id();
        decisionService.requireAccess(new AuthorizationRequest(userId, resourceId, authority));
    }
}
