package com.company.scopery.modules.workspace.joinrequest.application;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.integration.WorkspaceIamIntegrationService;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.joinrequest.application.command.CreateWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.command.ReviewWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.WorkspaceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WorkspaceJoinRequestApplicationService {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public WorkspaceJoinRequestApplicationService(
            WorkspaceJoinRequestRepository joinRequestRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository memberRepository,
            CurrentUserAuthorizationService currentUserService,
            WorkspaceIamIntegrationService iamIntegrationService,
            EmailNotificationTriggerPublisher notificationPublisher,
            WorkspaceActivityLogger activityLogger) {
        this.joinRequestRepository = joinRequestRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceJoinRequestResponse createJoinRequest(CreateWorkspaceJoinRequestCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();

        Workspace ws;
        if (command.workspaceId() != null) {
            ws = workspaceRepository.findById(command.workspaceId())
                    .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));
        } else if (command.workspaceCode() != null && !command.workspaceCode().isBlank()) {
            ws = workspaceRepository.findByCode(WorkspaceCode.of(command.workspaceCode()))
                    .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_NOT_FOUND,
                            "Workspace not found with code: " + command.workspaceCode(), null));
        } else {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_NOT_FOUND,
                    "Either workspaceId or workspaceCode must be provided", null);
        }

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }
        if (ws.joinPolicy() == WorkspaceJoinPolicy.INVITE_ONLY) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUIRES_INVITATION,
                    "This workspace only accepts members via invitation", null);
        }
        if (ws.joinPolicy() == WorkspaceJoinPolicy.DISABLED) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_DISABLED,
                    "This workspace is not accepting new members", null);
        }
        if (memberRepository.isActiveMember(ws.id(), currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_ALREADY_MEMBER,
                    "You are already a member of this workspace", null);
        }
        if (joinRequestRepository.findPendingByWorkspaceAndUser(ws.id(), currentUserId).isPresent()) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_ALREADY_PENDING,
                    "You already have a pending join request for this workspace", null);
        }

        WorkspaceJoinRequest request = WorkspaceJoinRequest.create(ws.id(), currentUserId, command.message());
        WorkspaceJoinRequest saved = joinRequestRepository.save(request);

        notificationPublisher.publish(new EmailNotificationTriggerPayload(
                null, "WORKSPACE", "WORKSPACE_JOIN_REQUEST_CREATED",
                ws.id(), currentUserId, Map.of("workspace.name", ws.name())));

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, saved.id(),
                WorkspaceActivityActions.CREATE_JOIN_REQUEST, "Join request created for: " + ws.code().value());

        return WorkspaceJoinRequestResponse.from(saved);
    }

    @Transactional
    public WorkspaceJoinRequestResponse approveJoinRequest(ReviewWorkspaceJoinRequestCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(command.workspaceId(), currentUserId, "WORKSPACE_MANAGE_JOIN_REQUEST");

        WorkspaceJoinRequest request = joinRequestRepository.findById(command.id())
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                        "Join request not found: " + command.id(), null));

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

    @Transactional
    public WorkspaceJoinRequestResponse rejectJoinRequest(ReviewWorkspaceJoinRequestCommand command) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(command.workspaceId(), currentUserId, "WORKSPACE_MANAGE_JOIN_REQUEST");

        WorkspaceJoinRequest request = joinRequestRepository.findById(command.id())
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                        "Join request not found: " + command.id(), null));

        WorkspaceJoinRequest rejected = request.reject(currentUserId, command.reviewNote());
        WorkspaceJoinRequest saved = joinRequestRepository.save(rejected);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, saved.id(),
                WorkspaceActivityActions.REJECT_JOIN_REQUEST, "Join request rejected");

        return WorkspaceJoinRequestResponse.from(saved);
    }

    @Transactional
    public WorkspaceJoinRequestResponse cancelJoinRequest(UUID id) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();

        WorkspaceJoinRequest request = joinRequestRepository.findById(id)
                .orElseThrow(() -> new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_NOT_FOUND,
                        "Join request not found: " + id, null));

        if (!request.requestedByUserId().equals(currentUserId)) {
            throw new AppException(WorkspaceErrorCatalog.WORKSPACE_JOIN_REQUEST_FORBIDDEN,
                    "You can only cancel your own join request", null);
        }

        WorkspaceJoinRequest cancelled = request.cancel();
        WorkspaceJoinRequest saved = joinRequestRepository.save(cancelled);

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_JOIN_REQUEST, saved.id(),
                WorkspaceActivityActions.CANCEL_JOIN_REQUEST, "Join request cancelled");

        return WorkspaceJoinRequestResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceJoinRequestResponse> listJoinRequests(UUID workspaceId, String status) {
        UUID currentUserId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireWorkspaceAccess(workspaceId, currentUserId, "WORKSPACE_MANAGE_JOIN_REQUEST");
        return joinRequestRepository.findByWorkspaceId(workspaceId, status)
                .stream().map(WorkspaceJoinRequestResponse::from).toList();
    }
}
