package com.company.scopery.modules.workspace.joinrequest.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class JoinRequestQueryService {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public JoinRequestQueryService(WorkspaceJoinRequestRepository joinRequestRepository,
                                    CurrentUserAuthorizationService currentUserService,
                                    WorkspaceIamIntegrationService iamIntegrationService) {
        this.joinRequestRepository = joinRequestRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceJoinRequestResponse> listJoinRequests(UUID workspaceId, String status) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(
                workspaceId, currentUserId, IamAuthorities.WORKSPACE_MANAGE_JOIN_REQUEST);
        return joinRequestRepository.findByWorkspaceId(workspaceId, status)
                .stream().map(WorkspaceJoinRequestResponse::from).toList();
    }
}
