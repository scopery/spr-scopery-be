package com.company.scopery.modules.workspace.joinrequest.application.action;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPayload;
import com.company.scopery.modules.notification.emailtrigger.domain.model.EmailNotificationTriggerPublisher;
import com.company.scopery.modules.workspace.joinrequest.application.command.CreateWorkspaceJoinRequestCommand;
import com.company.scopery.modules.workspace.joinrequest.application.response.WorkspaceJoinRequestResponse;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequest;
import com.company.scopery.modules.workspace.joinrequest.domain.model.WorkspaceJoinRequestRepository;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceErrorCatalog;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.valueobject.WorkspaceCode;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceJoinPolicy;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Component
public class CreateJoinRequestAction {

    private final WorkspaceJoinRequestRepository joinRequestRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final EmailNotificationTriggerPublisher notificationPublisher;
    private final WorkspaceActivityLogger activityLogger;

    public CreateJoinRequestAction(WorkspaceJoinRequestRepository joinRequestRepository,
                                    WorkspaceRepository workspaceRepository,
                                    WorkspaceMemberRepository memberRepository,
                                    CurrentUserAuthorizationService currentUserService,
                                    EmailNotificationTriggerPublisher notificationPublisher,
                                    WorkspaceActivityLogger activityLogger) {
        this.joinRequestRepository = joinRequestRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.currentUserService = currentUserService;
        this.notificationPublisher = notificationPublisher;
        this.activityLogger = activityLogger;
    }

    @Transactional
    public WorkspaceJoinRequestResponse execute(CreateWorkspaceJoinRequestCommand command) {
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
}
