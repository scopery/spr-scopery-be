package com.company.scopery.modules.workspace.orgmember.application.action;

import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberResponse;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipType;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.orgmember.application.service.OrgMembershipAccessRevocationService;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RemoveOrgMemberAction {

    private final OrgMemberRepository orgMemberRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final OrgMembershipAccessRevocationService accessRevocationService;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ImmutableAuditEventService auditEventService;

    public RemoveOrgMemberAction(OrgMemberRepository orgMemberRepository,
                                  WorkspaceActivityLogger activityLogger,
                                  OrgMembershipAccessRevocationService accessRevocationService,
                                  CurrentUserAuthorizationService currentUserService,
                                  WorkspaceIamIntegrationService iamIntegrationService,
                                  ImmutableAuditEventService auditEventService) {
        this.orgMemberRepository = orgMemberRepository;
        this.activityLogger = activityLogger;
        this.accessRevocationService = accessRevocationService;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public OrgMemberResponse execute(UUID memberId) {
        OrgMember member = orgMemberRepository.findById(memberId)
                .orElseThrow(() -> WorkspaceExceptions.orgMemberNotFound(memberId));

        if (member.membershipType() == OrgMembershipType.OWNER) {
            throw WorkspaceExceptions.orgMemberCannotRemoveOwner(member.userId());
        }

        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(member.organizationId(), actorId, IamAuthorities.ORGANIZATION_MANAGE);

        OrgMember removed = member.remove();
        OrgMember saved = orgMemberRepository.save(removed);
        accessRevocationService.revokeDescendantAccess(saved.organizationId(), saved.userId(), actorId);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_MEMBER, saved.id(),
                WorkspaceActivityActions.REMOVE_ORG_MEMBER,
                "Org member removed: userId=" + saved.userId() + " from orgId=" + saved.organizationId());
        auditEventService.record(AuditEventType.ORG_MEMBER_REMOVED,
                actorId, "USER", "ORG_MEMBER", saved.id(),
                saved.organizationId(), null, member, saved, "Member removed and descendant access revoked");

        return OrgMemberResponse.from(saved);
    }
}
