package com.company.scopery.modules.workspace.joinrequest.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.joinrequest.application.command.ReviewWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class ApproveJoinRequestAction {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public ApproveJoinRequestAction(WorkspaceJoinRequestRepository joinRequestRepository,
                                     WorkspaceMemberRepository memberRepository,
                                     CurrentUserAuthorizationService currentUserService,
                                     WorkspaceIamIntegrationService iamIntegrationService,
                                     EmailNotificationTriggerPublisher notificationPublisher,
                                     WorkspaceActivityLogger activityLogger) {
        this.joinRequestRepository = joinRequestRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
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

        WorkspaceJoinRequest approved = request.approve(currentUserId);
        joinRequestRepository.save(approved);

        memberRepository.save(WorkspaceMember.create(request.workspaceId(), request.requestedByUserId()));

        notificationPublisher.publish(new EmailNotificationTriggerPayload(
                null, "WORKSPACE", "WORKSPACE_JOIN_REQUEST_APPROVED",
                request.workspaceId(), currentUserId, Map.of()));

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, approved.id(),
                WorkspaceActivityActions.APPROVE_JOIN_REQUEST, "Join request approved");

        return WorkspaceJoinRequestResponse.from(approved);
    }
}
