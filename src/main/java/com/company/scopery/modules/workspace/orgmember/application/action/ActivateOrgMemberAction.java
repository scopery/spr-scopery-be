package com.company.scopery.modules.workspace.orgmember.application.action;

import com.company.scopery.modules.workspace.orgmember.application.command.ChangeOrgMemberStatusCommand;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberResponse;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;

import java.util.UUID;

@Component
public class ActivateOrgMemberAction {
    private final OrgMemberRepository repository;
    private final WorkspaceActivityLogger activityLogger;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final ImmutableAuditEventService auditEventService;

    public ActivateOrgMemberAction(OrgMemberRepository repository, WorkspaceActivityLogger activityLogger,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService,
                                   ImmutableAuditEventService auditEventService) {
        this.repository = repository;
        this.activityLogger = activityLogger;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public OrgMemberResponse execute(ChangeOrgMemberStatusCommand command) {
        OrgMember member = findInOrganization(command);
        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(command.organizationId(), actorId, IamAuthorities.ORGANIZATION_MANAGE);
        OrgMember saved = repository.save(member.activate());
        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_MEMBER, saved.id(),
                WorkspaceActivityActions.ACTIVATE_ORG_MEMBER, "Organization member activated: " + saved.userId());
        auditEventService.record(AuditEventType.ORG_MEMBER_ACTIVATED,
                actorId, "USER", "ORG_MEMBER", saved.id(),
                saved.organizationId(), null, member, saved, "Member activated");
        return OrgMemberResponse.from(saved);
    }

    private OrgMember findInOrganization(ChangeOrgMemberStatusCommand command) {
        OrgMember member = repository.findById(command.memberId())
                .orElseThrow(() -> WorkspaceExceptions.orgMemberNotFound(command.memberId()));
        if (!member.organizationId().equals(command.organizationId())) {
            throw WorkspaceExceptions.orgMemberNotFound(command.memberId());
        }
        return member;
    }
}
