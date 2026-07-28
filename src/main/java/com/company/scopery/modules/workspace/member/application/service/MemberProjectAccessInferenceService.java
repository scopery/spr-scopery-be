package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.common.pagination.PageQuery;
import com.company.scopery.common.pagination.PageResult;
import com.company.scopery.modules.iam.grant.domain.enums.IamAccessGrantStatus;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.project.project.domain.model.Project;
import com.company.scopery.modules.project.project.domain.model.ProjectRepository;
import com.company.scopery.modules.workspace.member.application.response.MemberProjectAccessSnapshot;
import com.company.scopery.modules.workspace.member.domain.enums.WorkspaceMemberStatus;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMember;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Infers Full workspace (ALL) vs Custom project access from live IAM grants (no migration).
 */
@Service
public class MemberProjectAccessInferenceService {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAuthResourceRepository resourceRepository;

    public MemberProjectAccessInferenceService(ProjectRepository projectRepository,
                                               WorkspaceMemberRepository workspaceMemberRepository,
                                               IamAccessGrantRepository grantRepository,
                                               IamAuthResourceRepository resourceRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.grantRepository = grantRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasFullProjectAccess(UUID workspaceId, UUID userId) {
        return hasFullProjectAccess(workspaceId, userId, null);
    }

    /**
     * Whether the member currently has full (ALL) project access.
     *
     * @param excludeProjectId when non-null (e.g. project being bootstrapped), that project is
     *                         ignored so existing ALL members stay ALL before their new grant exists
     */
    @Transactional(readOnly = true)
    public boolean hasFullProjectAccess(UUID workspaceId, UUID userId, UUID excludeProjectId) {
        List<Project> peerProjects = projectRepository.findAllByWorkspaceId(workspaceId).stream()
                .filter(project -> excludeProjectId == null || !excludeProjectId.equals(project.id()))
                .toList();
        Set<UUID> grantedProjectIds = collectGrantedProjectIds(workspaceId, userId);
        if (excludeProjectId != null) {
            grantedProjectIds.remove(excludeProjectId);
        }
        boolean activeMember = isActiveWorkspaceMember(workspaceId, userId);

        if (grantedProjectIds.isEmpty() && activeMember) {
            return true;
        }
        if (peerProjects.isEmpty()) {
            return true;
        }
        return grantedProjectIds.containsAll(projectIds(peerProjects));
    }

    @Transactional(readOnly = true)
    public MemberProjectAccessSnapshot buildSnapshot(UUID workspaceId, UUID userId) {
        List<Project> allProjects = projectRepository.findAllByWorkspaceId(workspaceId);
        List<MemberProjectAccessSnapshot.ProjectAccessItem> available = allProjects.stream()
                .map(MemberProjectAccessInferenceService::toItem)
                .toList();
        int totalProjects = allProjects.size();

        Set<UUID> grantedProjectIds = collectGrantedProjectIds(workspaceId, userId);
        boolean activeMember = isActiveWorkspaceMember(workspaceId, userId);

        String accessMode;
        List<MemberProjectAccessSnapshot.ProjectAccessItem> granted;

        if (grantedProjectIds.isEmpty() && activeMember) {
            // Legacy: no PROJECT grants yet → treat ACTIVE members as full workspace access.
            accessMode = MemberProjectAccessSnapshot.MODE_ALL;
            granted = available;
        } else if (!allProjects.isEmpty() && grantedProjectIds.containsAll(projectIds(allProjects))) {
            accessMode = MemberProjectAccessSnapshot.MODE_ALL;
            granted = available;
        } else if (allProjects.isEmpty() && grantedProjectIds.isEmpty()) {
            accessMode = MemberProjectAccessSnapshot.MODE_ALL;
            granted = available;
        } else {
            accessMode = MemberProjectAccessSnapshot.MODE_CUSTOM;
            Map<UUID, MemberProjectAccessSnapshot.ProjectAccessItem> byId = new LinkedHashMap<>();
            for (Project project : allProjects) {
                if (grantedProjectIds.contains(project.id())) {
                    byId.put(project.id(), toItem(project));
                }
            }
            granted = List.copyOf(byId.values());
        }

        return new MemberProjectAccessSnapshot(
                workspaceId, userId, accessMode, totalProjects, granted, available);
    }

    private Set<UUID> collectGrantedProjectIds(UUID workspaceId, UUID userId) {
        Set<UUID> granted = new LinkedHashSet<>();
        int page = 0;
        while (true) {
            PageResult<IamAccessGrant> grants = grantRepository.findAll(
                    userId, null, workspaceId, IamAccessGrantStatus.ACTIVE, PageQuery.of(page, 100));
            for (IamAccessGrant grant : grants.content()) {
                Optional<IamAuthResource> resource = resourceRepository.findById(grant.resourceId());
                if (resource.isEmpty() || resource.get().resourceType() != IamResourceType.PROJECT) {
                    continue;
                }
                UUID projectId = resource.get().refId();
                if (projectId != null) {
                    granted.add(projectId);
                }
            }
            if (grants.content().size() < 100 || grants.last()) {
                break;
            }
            page++;
        }
        return granted;
    }

    private boolean isActiveWorkspaceMember(UUID workspaceId, UUID userId) {
        Optional<WorkspaceMember> membership =
                workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
        return membership.isPresent()
                && membership.get().status() == WorkspaceMemberStatus.ACTIVE;
    }

    private static Set<UUID> projectIds(List<Project> projects) {
        Set<UUID> ids = new LinkedHashSet<>();
        for (Project project : projects) {
            ids.add(project.id());
        }
        return ids;
    }

    private static MemberProjectAccessSnapshot.ProjectAccessItem toItem(Project project) {
        return new MemberProjectAccessSnapshot.ProjectAccessItem(
                project.id(), project.name(), project.code());
    }
}
