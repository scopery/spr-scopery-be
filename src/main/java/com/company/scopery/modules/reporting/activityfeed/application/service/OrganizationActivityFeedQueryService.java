package com.company.scopery.modules.reporting.activityfeed.application.service;

import com.company.scopery.common.audit.ImmutableAuditEventJpaEntity;
import com.company.scopery.common.audit.ImmutableAuditEventJpaRepository;
import com.company.scopery.common.pagination.PageRequestUtils;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.reporting.activityfeed.application.response.ScopedActivityFeedItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrganizationActivityFeedQueryService {

    private final ImmutableAuditEventJpaRepository auditEvents;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final CurrentUserAuthorizationService currentUserService;

    public OrganizationActivityFeedQueryService(
            ImmutableAuditEventJpaRepository auditEvents,
            WorkspaceIamIntegrationService iamIntegrationService,
            CurrentUserAuthorizationService currentUserService) {
        this.auditEvents = auditEvents;
        this.iamIntegrationService = iamIntegrationService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public PageResult<ScopedActivityFeedItemResponse> list(UUID organizationId, int page, int size) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(organizationId, actorId, IamAuthorities.ORGANIZATION_MANAGE);

        Specification<ImmutableAuditEventJpaEntity> spec = (root, query, cb) ->
                cb.equal(root.get("organizationId"), organizationId);

        Page<ImmutableAuditEventJpaEntity> result = auditEvents.findAll(
                spec,
                PageRequestUtils.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt")));
        return PageResult.fromSpringPage(result.map(ScopedActivityFeedItemResponse::from));
    }
}
