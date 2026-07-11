package com.company.scopery.modules.iam.grant.application.action;

import com.company.scopery.modules.iam.grant.application.command.RevokeIamAccessGrantCommand;
import com.company.scopery.modules.iam.grant.application.response.IamAccessGrantResponse;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.authorization.application.service.AuthorizationDecisionService;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RevokeIamAccessGrantAction {

    private final IamAccessGrantRepository grantRepository;
    private final IamActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final AuthorizationDecisionService authorizationDecisionService;
    private final IamAuthResourceRepository resourceRepository;
    private final ImmutableAuditEventService auditEventService;

    public RevokeIamAccessGrantAction(IamAccessGrantRepository grantRepository,
                                      IamActivityLogger activityLogger,
                                      CurrentUserAuthorizationService currentUserService,
                                      AuthorizationDecisionService authorizationDecisionService,
                                      IamAuthResourceRepository resourceRepository,
                                      ImmutableAuditEventService auditEventService) {
        this.grantRepository = grantRepository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.authorizationDecisionService = authorizationDecisionService;
        this.resourceRepository = resourceRepository;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public IamAccessGrantResponse execute(RevokeIamAccessGrantCommand command) {
        UUID id = command.id();
        IamAccessGrant grant = grantRepository.findById(id)
                .orElseThrow(() -> IamExceptions.iamAccessGrantNotFound(id));
        var resource = resourceRepository.findById(grant.resourceId())
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(grant.resourceId()));
        UUID actorId = currentUserService.resolveCurrentUser().id();
        if (!actorId.equals(grant.grantedBy())) {
            var authority = switch (resource.resourceType()) {
                case ORGANIZATION -> IamAuthorities.ORGANIZATION_MANAGE;
                case WORKSPACE -> IamAuthorities.WORKSPACE_ACCESS_MANAGE_ACCESS;
                case TEAM -> IamAuthorities.TEAM_MANAGE;
                case GLOBAL -> IamAuthorities.SYSTEM_IAM_MANAGE_ACCESS_GRANT;
            };
            authorizationDecisionService.requireAccess(new AuthorizationRequest(
                    actorId, grant.resourceId(), authority));
        }
        IamAccessGrant saved = grantRepository.save(grant.revoke());

        activityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.REVOKE_IAM_ACCESS_GRANT,
                "Access grant revoked: " + saved.id());
        auditEventService.record(AuditEventType.ACCESS_GRANT_REVOKED, actorId, "USER",
                "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(),
                resource.workspaceId(), grant, saved, "Access grant revoked");

        return IamAccessGrantResponse.from(saved);
    }
}
