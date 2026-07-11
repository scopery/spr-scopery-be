package com.company.scopery.modules.workspace.invitation.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.invitation.application.response.WorkspaceInvitationResponse;
import com.company.scopery.modules.workspace.invitation.domain.model.WorkspaceInvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InvitationQueryService {

    private final WorkspaceInvitationRepository invitationRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public InvitationQueryService(WorkspaceInvitationRepository invitationRepository,
                                   CurrentUserAuthorizationService currentUserService,
                                   WorkspaceIamIntegrationService iamIntegrationService) {
        this.invitationRepository = invitationRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceInvitationResponse> listInvitations(UUID workspaceId) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(
                workspaceId, currentUserId, IamAuthorities.WORKSPACE_MANAGE_INVITATION);
        return invitationRepository.findByWorkspaceId(workspaceId).stream()
                .map(WorkspaceInvitationResponse::from)
                .toList();
    }
}
