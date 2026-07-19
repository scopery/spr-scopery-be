package com.company.scopery.modules.workspace.member.application.service;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Cascades workspace-scoped access revocation when a workspace member is deactivated.
 */
@Service
public class WorkspaceMembershipAccessRevocationService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final IamActivityLogger iamActivityLogger;
    private final ImmutableAuditEventService auditEventService;

    public WorkspaceMembershipAccessRevocationService(TeamMemberRepository teamMemberRepository,
                                                      TeamRepository teamRepository,
                                                      IamAuthResourceRepository resourceRepository,
                                                      IamAccessGrantRepository grantRepository,
                                                      IamRoleAssignmentRepository roleAssignmentRepository,
                                                      IamActivityLogger iamActivityLogger,
                                                      ImmutableAuditEventService auditEventService) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.resourceRepository = resourceRepository;
        this.grantRepository = grantRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.iamActivityLogger = iamActivityLogger;
        this.auditEventService = auditEventService;
    }

    public void revokeWorkspaceScopedAccess(UUID workspaceId, UUID userId, UUID actorId) {
        teamMemberRepository.findByUserId(userId).forEach(member ->
                teamRepository.findById(member.teamId())
                        .filter(team -> workspaceId.equals(team.workspaceId()))
                        .ifPresent(team -> teamMemberRepository.delete(team.id(), userId)));

        Set<UUID> revokedGrantIds = new HashSet<>();
        resourceRepository.findByRefIdAndResourceType(workspaceId, IamResourceType.WORKSPACE).ifPresent(wsResource -> {
            revokeUserGrantsOnResource(wsResource, userId, actorId, revokedGrantIds);
            if (wsResource.organizationId() != null) {
                resourceRepository.findAllByOrganizationId(wsResource.organizationId()).stream()
                        .filter(resource -> workspaceId.equals(resource.workspaceId())
                                || (resource.resourceType() == IamResourceType.WORKSPACE
                                && workspaceId.equals(resource.refId())))
                        .forEach(resource -> revokeUserGrantsOnResource(resource, userId, actorId, revokedGrantIds));
            }
        });

        roleAssignmentRepository.findActiveByAssigneeId(userId).stream()
                .filter(assignment -> workspaceId.equals(assignment.workspaceId()))
                .forEach(assignment -> {
                    var saved = roleAssignmentRepository.save(assignment.deactivate());
                    iamActivityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                            IamActivityActions.DEACTIVATE_IAM_ROLE_ASSIGNMENT,
                            "Role assignment deactivated by workspace membership cascade: " + saved.id());
                });
    }

    private void revokeUserGrantsOnResource(IamAuthResource resource, UUID userId, UUID actorId,
                                            Set<UUID> revokedGrantIds) {
        grantRepository.findActiveBySubjectsAndResource(
                        List.of(IamSubjectType.USER), List.of(userId), resource.id())
                .forEach(grant -> {
                    if (revokedGrantIds.add(grant.id())) {
                        revokeGrant(grant, resource, actorId);
                    }
                });
    }

    private void revokeGrant(IamAccessGrant grant, IamAuthResource resource, UUID actorId) {
        IamAccessGrant saved = grantRepository.save(grant.revoke());
        iamActivityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.REVOKE_IAM_ACCESS_GRANT,
                "Access grant revoked by workspace membership cascade: " + saved.id());
        auditEventService.record(AuditEventType.ACCESS_GRANT_REVOKED, actorId, "USER",
                "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(),
                resource.workspaceId(), grant, saved, "Access grant revoked by workspace membership cascade");
    }
}
