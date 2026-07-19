package com.company.scopery.modules.workspace.organization.application.action;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.resource.application.service.IamAuthResourceLifecycleService;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.workspace.organization.application.response.OrganizationResponse;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArchiveOrganizationAction {

    private final OrganizationRepository organizationRepository;
    private final IamAuthResourceLifecycleService authResourceLifecycleService;
    private final WorkspaceActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;
    private final CurrentUserAuthorizationService currentUserService;

    public ArchiveOrganizationAction(OrganizationRepository organizationRepository,
                                      IamAuthResourceLifecycleService authResourceLifecycleService,
                                      WorkspaceActivityLogger activityLogger,
                                      ImmutableAuditEventService auditEventService,
                                      CurrentUserAuthorizationService currentUserService) {
        this.organizationRepository = organizationRepository;
        this.authResourceLifecycleService = authResourceLifecycleService;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public OrganizationResponse execute(UUID id) {
        Organization org = findOrThrow(id);
        Organization archived = org.archive();
        Organization saved = organizationRepository.save(archived);
        authResourceLifecycleService.deactivateByRef(saved.id(), IamResourceType.ORGANIZATION);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORGANIZATION, saved.id(),
                WorkspaceActivityActions.ARCHIVE_ORGANIZATION,
                "Organization archived: " + saved.code().value());

        UUID actorId = currentUserService.resolveCurrentUser().id();
        auditEventService.record(AuditEventType.ORGANIZATION_ARCHIVED, actorId, "USER",
                "ORGANIZATION", saved.id(), saved.id(), null, null,
                java.util.Map.of("status", saved.status().name()), "Organization archived");

        return OrganizationResponse.from(saved);
    }

    private Organization findOrThrow(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> WorkspaceExceptions.organizationNotFound(id));
    }
}
