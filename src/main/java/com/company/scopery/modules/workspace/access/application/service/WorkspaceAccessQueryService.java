package com.company.scopery.modules.workspace.access.application.service;

import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceAccessResponse;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceEffectiveAccessEntry;
import com.company.scopery.modules.workspace.access.application.response.WorkspaceSubjectAccessResponse;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.shared.error.WorkspaceExceptions;
import com.company.scopery.modules.workspace.workspace.domain.model.Workspace;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class WorkspaceAccessQueryService {
    private final IamAuthResourceRepository resourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final WorkspaceRepository workspaceRepository;
    private final IamUserRepository userRepository;

    public WorkspaceAccessQueryService(IamAuthResourceRepository resourceRepository,
                                       IamAccessGrantRepository grantRepository,
                                       OrgTeamMemberRepository orgTeamMemberRepository,
                                       IamRoleAssignmentRepository roleAssignmentRepository,
                                       WorkspaceRepository workspaceRepository,
                                       IamUserRepository userRepository) {
        this.resourceRepository = resourceRepository;
        this.grantRepository = grantRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkspaceEffectiveAccessEntry> findWorkspacesForUser(UUID userId) {
        return resourceRepository.findAllByResourceTypeAndStatus(IamResourceType.WORKSPACE, IamResourceStatus.ACTIVE)
                .stream()
                .map(resource -> new ResourceAccess(resource, effectiveUserAccess(resource, userId)))
                .filter(candidate -> candidate.access().allowed())
                .map(candidate -> toWorkspaceEntry(candidate.access(), candidate.resource()))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasEffectiveAccess(UUID userId, UUID workspaceId) {
        IamAuthResource resource = workspaceResource(workspaceId);
        return effectiveUserAccess(resource, userId).allowed();
    }

    @Transactional(readOnly = true)
    public WorkspaceSubjectAccessResponse explainUserAccess(UUID workspaceId, UUID userId) {
        return effectiveUserAccess(workspaceResource(workspaceId), userId);
    }

    @Transactional(readOnly = true)
    public WorkspaceSubjectAccessResponse explainSubjectAccess(UUID workspaceId, IamSubjectType subjectType,
                                                               UUID subjectId) {
        IamAuthResource resource = workspaceResource(workspaceId);
        if (subjectType == IamSubjectType.USER) {
            return effectiveUserAccess(resource, subjectId);
        }
        List<IamAccessGrant> grants = grantRepository.findActiveBySubjectsAndResource(
                List.of(subjectType), List.of(subjectId), resource.id());
        return summarize(subjectType, subjectId, grants, false);
    }

    @Transactional(readOnly = true)
    public WorkspaceAccessResponse listWorkspaceAccess(UUID workspaceId) {
        IamAuthResource resource = workspaceResource(workspaceId);
        Map<SubjectKey, List<IamAccessGrant>> bySubject = new LinkedHashMap<>();
        grantRepository.findActiveByResource(resource.id()).forEach(grant ->
                bySubject.computeIfAbsent(new SubjectKey(grant.subjectType(), grant.subjectId()), ignored -> new ArrayList<>())
                        .add(grant));
        List<WorkspaceSubjectAccessResponse> subjects = bySubject.entrySet().stream()
                .map(entry -> summarize(entry.getKey().type(), entry.getKey().id(), entry.getValue(),
                        entry.getKey().type() == IamSubjectType.USER
                                && resource.isOwnedBy(entry.getKey().id())))
                .toList();
        return new WorkspaceAccessResponse(workspaceId, subjects);
    }

    private WorkspaceSubjectAccessResponse effectiveUserAccess(IamAuthResource resource, UUID userId) {
        boolean userActive = userRepository.findById(userId)
                .map(user -> user.status() == IamUserStatus.ACTIVE)
                .orElse(false);
        if (!userActive) {
            return new WorkspaceSubjectAccessResponse(
                    IamSubjectType.USER.name(), userId, false, List.of(), List.of());
        }

        List<IamSubjectType> subjectTypes = new ArrayList<>();
        List<UUID> subjectIds = new ArrayList<>();
        subjectTypes.add(IamSubjectType.USER);
        subjectIds.add(userId);

        orgTeamMemberRepository.findAllByUserId(userId).forEach(member -> {
            subjectTypes.add(IamSubjectType.TEAM);
            subjectIds.add(member.teamId());
        });
        roleAssignmentRepository.findActiveByAssigneeId(userId).stream()
                .filter(assignment -> assignment.workspaceId() == null
                        || assignment.workspaceId().equals(resource.refId()))
                .forEach(assignment -> {
                    subjectTypes.add(IamSubjectType.ROLE);
                    subjectIds.add(assignment.roleId());
                });

        List<IamAccessGrant> grants = grantRepository.findActiveBySubjectsAndResource(
                subjectTypes, subjectIds, resource.id());
        return summarize(IamSubjectType.USER, userId, grants, resource.isOwnedBy(userId));
    }

    private WorkspaceSubjectAccessResponse summarize(IamSubjectType requestedType, UUID requestedId,
                                                      List<IamAccessGrant> grants, boolean owner) {
        boolean denied = grants.stream().anyMatch(g -> g.effect() == IamGrantEffect.DENY);
        Set<String> sources = new LinkedHashSet<>();
        if (owner) sources.add("OWNER_POLICY");
        grants.stream().filter(g -> g.effect() == IamGrantEffect.ALLOW).forEach(g -> sources.add(sourceOf(g)));
        boolean allowed = !denied && (owner || !sources.isEmpty());
        List<UUID> grantIds = grants.stream().map(IamAccessGrant::id).toList();
        return new WorkspaceSubjectAccessResponse(
                requestedType.name(), requestedId, allowed, List.copyOf(sources), grantIds);
    }

    private String sourceOf(IamAccessGrant grant) {
        return switch (grant.subjectType()) {
            case USER -> "DIRECT_USER_GRANT";
            case TEAM -> "TEAM_GRANT";
            case ROLE -> "ROLE_ASSIGNMENT";
        };
    }

    private WorkspaceEffectiveAccessEntry toWorkspaceEntry(WorkspaceSubjectAccessResponse access,
                                                            IamAuthResource resource) {
        Workspace workspace = workspaceRepository.findById(resource.refId())
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(resource.refId()));
        return new WorkspaceEffectiveAccessEntry(workspace.id(), workspace.code().value(), workspace.name(),
                access.accessSources(), access.contributingGrantIds());
    }

    private IamAuthResource workspaceResource(UUID workspaceId) {
        return resourceRepository.findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE)
                .orElseThrow(() -> WorkspaceExceptions.workspaceNotFound(workspaceId));
    }

    private record SubjectKey(IamSubjectType type, UUID id) {
    }

    private record ResourceAccess(IamAuthResource resource, WorkspaceSubjectAccessResponse access) {
    }
}
