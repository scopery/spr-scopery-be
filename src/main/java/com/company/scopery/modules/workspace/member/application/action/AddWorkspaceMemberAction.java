package com.company.scopery.modules.workspace.member.application.action;

import com.company.scopery.modules.workspace.member.application.command.AddWorkspaceMemberCommand;
import com.company.scopery.modules.workspace.member.application.response.WorkspaceMemberResponse;
import com.company.scopery.modules.workspace.member.application.service.WorkspaceMembershipEnrollmentService;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.activity.WorkspaceActivityLogger;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceActivityActions;
import com.company.scopery.modules.workspace.shared.constant.WorkspaceEntityTypes;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.shared.service.InAppDeliveryService;
import com.company.scopery.modules.workspace.shared.service.WorkspaceAudienceResolver;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import com.company.scopery.modules.workspace.workspace.domain.enums.WorkspaceStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AddWorkspaceMemberAction {

    private final WorkspaceMembershipEnrollmentService enrollmentService;
    private final WorkspaceRepository workspaceRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final WorkspaceActivityLogger activityLogger;
    private final InAppDeliveryService inAppDeliveryService;
    private final WorkspaceAudienceResolver audienceResolver;

    public AddWorkspaceMemberAction(WorkspaceMembershipEnrollmentService enrollmentService,
                                     WorkspaceRepository workspaceRepository,
                                     OrgMemberRepository orgMemberRepository,
                                     WorkspaceActivityLogger activityLogger,
                                     InAppDeliveryService inAppDeliveryService,
                                     WorkspaceAudienceResolver audienceResolver) {
        this.enrollmentService = enrollmentService;
        this.workspaceRepository = workspaceRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.activityLogger = activityLogger;
        this.inAppDeliveryService = inAppDeliveryService;
        this.audienceResolver = audienceResolver;
    }

    @Transactional
    public WorkspaceMemberResponse execute(AddWorkspaceMemberCommand command) {
        Workspace ws = workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(command.workspaceId()));

        if (ws.status() != WorkspaceStatus.ACTIVE) {
            throw WorkspaceExceptions.workspaceNotActive(ws.code().value());
        }

        if (!orgMemberRepository.isActiveMember(ws.organizationId(), command.userId())) {
            throw WorkspaceExceptions.orgTeamMemberRequiresOrgMember(command.userId(), ws.organizationId());
        }

        WorkspaceMember saved = enrollmentService.ensureActiveMembership(
                command.workspaceId(), command.userId(), true);

        inAppDeliveryService.deliverNotificationFyi(
                audienceResolver.explicitUser(saved.userId()),
                "WORKSPACE_MEMBER_ADDED",
                saved.id(),
                ws.organizationId(),
                command.workspaceId(),
                "Added to workspace",
                "You were added to a workspace.");

        activityLogger.logSuccess(WorkspaceEntityTypes.WORKSPACE_MEMBER, saved.id(),
                WorkspaceActivityActions.ADD_WORKSPACE_MEMBER,
                "Workspace member added: userId=" + saved.userId() + " to workspaceId=" + saved.workspaceId());

        return WorkspaceMemberResponse.from(saved);
    }
}
