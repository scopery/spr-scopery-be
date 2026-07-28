package com.company.scopery.modules.workspace.joinrequest.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.workspace.joinrequest.application.command.CancelJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.InvitationInboxCleanupService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class CancelJoinRequestAction {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceActivityLogger activityLogger;
    private final InvitationInboxCleanupService inboxCleanupService;

    public CancelJoinRequestAction(WorkspaceJoinRequestRepository joinRequestRepository,
                                    CurrentUserAuthorizationService currentUserService,
                                    WorkspaceActivityLogger activityLogger,
                                    InvitationInboxCleanupService inboxCleanupService) {
        this.joinRequestRepository = joinRequestRepository;
        this.currentUserService = currentUserService;
        this.activityLogger = activityLogger;
        this.inboxCleanupService = inboxCleanupService;
    }

    @Transactional
    public WorkspaceJoinRequestResponse execute(CancelJoinRequestCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();

        WorkspaceJoinRequest request = joinRequestRepository.findById(command.id())
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                        "Join request not found: " + command.id(), null));

        if (!request.requestedByUserId().equals(currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_FORBIDDEN,
                    "You can only cancel your own join request", null);
        }

        WorkspaceJoinRequest cancelled = request.cancel();
        WorkspaceJoinRequest saved = joinRequestRepository.save(cancelled);

        inboxCleanupService.dismissBySourceForAll(
                InAppDeliveryService.SOURCE_JOIN_REQUEST, saved.id());

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, saved.id(),
                WorkspaceActivityActions.CANCEL_JOIN_REQUEST, "Join request cancelled");

        return WorkspaceJoinRequestResponse.from(saved);
    }
}
