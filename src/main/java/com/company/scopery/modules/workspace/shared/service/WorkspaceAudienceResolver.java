package com.company.scopery.modules.workspace.shared.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.modules.iam.grant.application.service.WorkspaceIamIntegrationService;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Resolves explicit recipient user sets for in-app delivery (Work Inbox / FYI Notification).
 */
@Service
public class WorkspaceAudienceResolver {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final WorkspaceIamIntegrationService iamIntegrationService;

    public WorkspaceAudienceResolver(WorkspaceRepository workspaceRepository,
                                      WorkspaceMemberRepository workspaceMemberRepository,
                                      WorkspaceIamIntegrationService iamIntegrationService) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.iamIntegrationService = iamIntegrationService;
    }

    public Set<UUID> explicitUser(UUID userId) {
        Set<UUID> out = new LinkedHashSet<>();
        if (userId != null) out.add(userId);
        return out;
    }

    @Transactional(readOnly = true)
    public Set<UUID> workspaceOwner(UUID workspaceId) {
        Set<UUID> out = new LinkedHashSet<>();
        workspaceRepository.findById(workspaceId)
                .map(Workspace::ownerUserId)
                .ifPresent(out::add);
        return out;
    }

    /**
     * Active workspace members (plus owner) who pass {@code canWorkspaceAccess} for the given right.
     */
    @Transactional(readOnly = true)
    public Set<UUID> usersWithWorkspaceRight(UUID workspaceId, IamPermissionAction authority) {
        Set<UUID> candidates = new LinkedHashSet<>();
        workspaceRepository.findById(workspaceId)
                .map(Workspace::ownerUserId)
                .ifPresent(candidates::add);

        var page = workspaceMemberRepository.findAll(
                workspaceId, null, WorkspaceMemberStatus.ACTIVE, PageQuery.of(0, 100));
        for (WorkspaceMember member : page.content()) {
            candidates.add(member.userId());
        }

        Set<UUID> allowed = new LinkedHashSet<>();
        for (UUID userId : candidates) {
            if (iamIntegrationService.canWorkspaceAccess(workspaceId, userId, authority)) {
                allowed.add(userId);
            }
        }
        return allowed;
    }
}
