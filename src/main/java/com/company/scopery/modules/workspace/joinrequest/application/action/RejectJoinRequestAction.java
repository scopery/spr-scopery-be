package com.company.scopery.modules.workspace.joinrequest.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.joinrequest.application.command.ReviewWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class RejectJoinRequestAction {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final WorkspaceActivityLogger activityLogger;
    private final InvitationInboxCleanupService inboxCleanupService;
    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public RejectJoinRequestAction(WorkspaceJoinRequestRepository joinRequestRepository,
                                    WorkspaceRepository workspaceRepository,
                                    CurrentUserAuthorizationService currentUserService,
                                    WorkspaceIamIntegrationService iamIntegrationService,
                                    WorkspaceActivityLogger activityLogger,
                                    InvitationInboxCleanupService inboxCleanupService,
                                    InAppDeliveryService inAppDeliveryService,
                                    WorkspaceAudienceResolver audienceResolver) {
        this.joinRequestRepository = joinRequestRepository;
        this.workspaceRepository = workspaceRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.activityLogger = activityLogger;
        this.inboxCleanupService = inboxCleanupService;
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional
    public WorkspaceJoinRequestResponse execute(ReviewWorkspaceJoinRequestCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(
                command.workspaceId(), currentUserId, IamAuthorities.WORKSPACE_MANAGE_JOIN_REQUEST);

        WorkspaceJoinRequest request = joinRequestRepository.findById(command.id())
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                        "Join request not found: " + command.id(), null));

        if (!request.workspaceId().equals(command.workspaceId())) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                    "Join request not found: " + command.id(), null);
        }

        WorkspaceJoinRequest rejected = request.reject(currentUserId, command.reviewNote());
        WorkspaceJoinRequest saved = joinRequestRepository.save(rejected);

        inboxCleanupService.dismissBySourceForAll(
                InAppDeliveryService.SOURCE_JOIN_REQUEST, saved.id());

        UUID organizationId = workspaceRepository.findById(request.workspaceId())
                .map(ws -> ws.organizationId())
                .orElse(null);
        inAppDeliveryService.deliverNotificationFyi(
                audienceResolver.explicitUser(request.requestedByUserId()),
                "JOIN_REQUEST_REJECTED",
                saved.id(),
                organizationId,
                request.workspaceId(),
                "Join request rejected",
                "Your request to join the workspace was rejected.");

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, saved.id(),
                WorkspaceActivityActions.REJECT_JOIN_REQUEST, "Join request rejected");

        return WorkspaceJoinRequestResponse.from(saved);
    }
}
