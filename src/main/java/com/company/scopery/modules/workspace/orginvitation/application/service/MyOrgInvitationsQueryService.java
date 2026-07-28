package com.company.scopery.modules.workspace.orginvitation.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.workspace.orginvitation.application.response.MyOrgInvitationResponse;
import com.company.scopery.modules.workspace.orginvitation.domain.enums.OrgInvitationStatus;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitation;
import com.company.scopery.modules.workspace.orginvitation.domain.model.OrgInvitationRepository;
import com.company.scopery.modules.workspace.organization.domain.model.Organization;
import com.company.scopery.modules.workspace.organization.domain.model.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyOrgInvitationsQueryService {

    private final OrgInvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final CurrentUserAuthorizationService currentUserAuthorizationService;

    public MyOrgInvitationsQueryService(OrgInvitationRepository invitationRepository,
                                         OrganizationRepository organizationRepository,
                                         CurrentUserAuthorizationService currentUserAuthorizationService) {
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.currentUserAuthorizationService = currentUserAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<MyOrgInvitationResponse> listPendingForCurrentUser() {
        IamUser user = currentUserAuthorizationService.resolveCurrentUser();
        String email = user.email().value();
        List<OrgInvitation> pending = invitationRepository.findByInviteeEmailIgnoreCaseAndStatus(
                email, OrgInvitationStatus.PENDING.name());

        List<MyOrgInvitationResponse> out = new ArrayList<>();
        for (OrgInvitation inv : pending) {
            if (inv.isExpired()) continue;
            String orgName = organizationRepository.findById(inv.organizationId())
                    .map(Organization::name)
                    .orElse("Organization");
            out.add(new MyOrgInvitationResponse(
                    inv.id(),
                    inv.organizationId(),
                    orgName,
                    inv.inviteeEmail(),
                    inv.membershipType().name(),
                    inv.status().name(),
                    inv.expiresAt(),
                    inv.createdAt()));
        }
        return out;
    }
}
