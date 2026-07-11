package com.company.scopery.modules.iam.authorization.application.service;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.resource.application.listeners.IamSystemAuthResourceInitializer;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.valueobject.IamResourceCode;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Guards system-scoped operations using IAM rights, delegating the actual per-right
 * decision to AuthorizationDecisionService against the seeded GLOBAL_SYSTEM resource
 * (see IamSystemAuthResourceInitializer). Which subjects hold which system rights on
 * that resource is controlled entirely by IamAccessGrant rows (see IamSystemGrantInitializer
 * for the SUPER_ADMIN bootstrap grant) — this class does not special-case any role.
 */
@Service
public class IamSystemAuthorizationService {

    private final CurrentUserAuthorizationService currentUserService;
    private final IamAuthResourceRepository resourceRepository;
    private final AuthorizationDecisionService decisionService;

    public IamSystemAuthorizationService(CurrentUserAuthorizationService currentUserService,
                                          IamAuthResourceRepository resourceRepository,
                                          AuthorizationDecisionService decisionService) {
        this.currentUserService = currentUserService;
        this.resourceRepository = resourceRepository;
        this.decisionService = decisionService;
    }

    /**
     * Throws 403 if the current user does not hold the given system right (via an active
     * ALLOW grant, direct or through a role/team) on the GLOBAL_SYSTEM resource.
     *
     * @param rightCode the system right code required
     */
    public void requireSystemRight(String rightCode) {
        IamUser user = currentUserService.resolveCurrentUser();
        UUID globalResourceId = globalSystemResourceId();
        decisionService.requireAccess(new AuthorizationRequest(user.id(), globalResourceId, rightCode));
    }

    /**
     * Returns true if the given user holds the given system right on the GLOBAL_SYSTEM resource.
     */
    public boolean hasSystemRight(UUID userId, String rightCode) {
        UUID globalResourceId = globalSystemResourceId();
        AuthorizationDecision decision = decisionService.canAccess(
                new AuthorizationRequest(userId, globalResourceId, rightCode));
        return decision.allowed();
    }

    private UUID globalSystemResourceId() {
        return resourceRepository
                .findByCodeAndResourceType(
                        IamResourceCode.of(IamSystemAuthResourceInitializer.GLOBAL_SYSTEM_RESOURCE_CODE),
                        IamResourceType.GLOBAL)
                .map(IamAuthResource::id)
                .orElseThrow(IamExceptions::globalSystemResourceMissing);
    }
}
