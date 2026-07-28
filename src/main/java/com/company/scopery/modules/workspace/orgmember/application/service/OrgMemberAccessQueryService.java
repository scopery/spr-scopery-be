package com.company.scopery.modules.workspace.orgmember.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.workspace.member.application.response.MemberProjectAccessSnapshot;
import com.company.scopery.modules.workspace.member.application.service.MemberProjectAccessInferenceService;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.orgmember.application.response.OrgMemberAccessResponse;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMember;
import com.company.scopery.modules.workspace.orgmember.domain.model.OrgMemberRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrgMemberAccessQueryService {

    private final OrgMemberRepository orgMemberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final CurrentUserAuthorizationService currentUserService;
    private final WorkspaceIamIntegrationService iamIntegrationService;
    private final MemberProjectAccessInferenceService projectAccessInferenceService;

    public OrgMemberAccessQueryService(OrgMemberRepository orgMemberRepository,
                                        WorkspaceRepository workspaceRepository,
                                        WorkspaceMemberRepository workspaceMemberRepository,
                                        CurrentUserAuthorizationService currentUserService,
                                        WorkspaceIamIntegrationService iamIntegrationService,
                                        MemberProjectAccessInferenceService projectAccessInferenceService) {
        this.orgMemberRepository = orgMemberRepository;
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.currentUserService = currentUserService;
        this.iamIntegrationService = iamIntegrationService;
        this.projectAccessInferenceService = projectAccessInferenceService;
    }

    @Transactional(readOnly = true)
    public OrgMemberAccessResponse getAccess(UUID organizationId, UUID userId) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        iamIntegrationService.requireOrgAccess(organizationId, actorId, IamAuthorities.ORGANIZATION_MANAGE);

        OrgMember orgMember = orgMemberRepository.findByOrganizationIdAndUserId(organizationId, userId)
                .orElseThrow(() -> WorkspaceExceptions.orgMemberNotFound(userId));

        List<Workspace> workspaces = workspaceRepository.findAllActiveByOrganizationId(organizationId);
        List<OrgMemberAccessResponse.WorkspaceMembershipAccessItem> items = new ArrayList<>();

        for (Workspace ws : workspaces) {
            Optional<WorkspaceMember> membership =
                    workspaceMemberRepository.findByWorkspaceIdAndUserId(ws.id(), userId);
            if (membership.isEmpty()) {
                continue;
            }
            MemberProjectAccessSnapshot snapshot = buildWorkspaceProjectAccessSnapshot(ws.id(), userId);
            items.add(new OrgMemberAccessResponse.WorkspaceMembershipAccessItem(
                    ws.id(),
                    ws.name(),
                    membership.get().status().name(),
                    snapshot.accessMode(),
                    snapshot.totalProjects(),
                    mapProjects(snapshot.projects()),
                    mapProjects(snapshot.availableProjects())));
        }

        return new OrgMemberAccessResponse(
                organizationId,
                userId,
                orgMember.status().name(),
                items);
    }

    /**
     * Snapshot builder shared by org GET, workspace GET, and project-access PUT.
     */
    @Transactional(readOnly = true)
    public MemberProjectAccessSnapshot buildWorkspaceProjectAccessSnapshot(UUID workspaceId, UUID userId) {
        return projectAccessInferenceService.buildSnapshot(workspaceId, userId);
    }

    @Transactional(readOnly = true)
    public boolean hasFullProjectAccess(UUID workspaceId, UUID userId) {
        return projectAccessInferenceService.hasFullProjectAccess(workspaceId, userId);
    }

    private static List<OrgMemberAccessResponse.ProjectAccessItem> mapProjects(
            List<MemberProjectAccessSnapshot.ProjectAccessItem> items) {
        return items.stream()
                .map(p -> new OrgMemberAccessResponse.ProjectAccessItem(
                        p.projectId(), p.projectName(), p.projectCode()))
                .toList();
    }
}
