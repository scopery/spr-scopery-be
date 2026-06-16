package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.modules.iam.authorization.domain.AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthorizationGuard {

    private final CurrentUserAuthorizationService currentUserService;
    private final AuthorizationDecisionService decisionService;

    public AuthorizationGuard(CurrentUserAuthorizationService currentUserService,
                               AuthorizationDecisionService decisionService) {
        this.currentUserService = currentUserService;
        this.decisionService = decisionService;
    }

    public void requireView(UUID resourceId) {
        require(resourceId, "VIEW");
    }

    public void requireCreate(UUID resourceId) {
        require(resourceId, "CREATE");
    }

    public void requireUpdate(UUID resourceId) {
        require(resourceId, "UPDATE");
    }

    public void requireDelete(UUID resourceId) {
        require(resourceId, "DELETE");
    }

    public void requireManagePermission(UUID resourceId) {
        require(resourceId, "MANAGE_PERMISSION");
    }

    public void require(UUID resourceId, String rightCode) {
        UUID userId = currentUserService.resolveCurrentUser().id();
        decisionService.requireAccess(new AuthorizationRequest(userId, resourceId, rightCode));
    }
}
