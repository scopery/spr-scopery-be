package com.company.scopery.modules.workspace.orginvitation.application.action;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.orgmember.domain.enums.OrgMembershipSource;
import com.company.scopery.modules.workspace.orginvitation.application.command.AcceptOrgInvitationCommand;
import com.company.scopery.modules.workspace.orginvitation.application.response.OrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class AcceptOrgInvitationAction {

    private final OrgInvitationRepository invitationRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;
    private final WorkspaceActivityLogger activityLogger;

    public AcceptOrgInvitationAction(OrgInvitationRepository invitationRepository,
                                      OrgMemberRepository orgMemberRepository,
                                      CurrentUserAuthorizationService currentUserAuthorizationService,
                                      WorkspaceActivityLogger activityLogger) {
        this.invitationRepository = invitationRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public OrgInvitationResponse execute(AcceptOrgInvitationCommand command) {
        OrgInvitation invitation = invitationRepository.findByToken(command.token())
                .orElseThrow(() -> WorkspaceExceptions.orgInvitationNotFound(command.token()));

        if (invitation.status() != OrgInvitationStatus.PENDING) {
            throw WorkspaceExceptions.orgInvitationNotPending(invitation.id());
        }
        if (invitation.isExpired()) {
            throw WorkspaceExceptions.orgInvitationExpired(invitation.id());
        }

        UUID userId = currentUserAuthorizationService.resolveCurrentUser().id();

        if (orgMemberRepository.existsByOrganizationIdAndUserId(invitation.organizationId(), userId)) {
            throw WorkspaceExceptions.orgInvitationAlreadyMember(invitation.organizationId(), userId);
        }

        OrgMember member = OrgMember.create(invitation.organizationId(), userId, invitation.membershipType(),
                OrgMembershipSource.ORGANIZATION_INVITATION);
        orgMemberRepository.save(member);

        OrgInvitation accepted = invitation.accept(userId);
        OrgInvitation saved = invitationRepository.save(accepted);

        activityLogger.logSuccess(WorkspaceEntityTypes.ORG_INVITATION, saved.id(),
                WorkspaceActivityActions.ACCEPT_ORG_INVITATION,
                "Org invitation accepted by userId=" + userId);

        return OrgInvitationResponse.from(saved);
    }
}
