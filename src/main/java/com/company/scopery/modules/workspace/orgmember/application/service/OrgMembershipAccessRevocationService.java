package com.company.scopery.modules.workspace.orgmember.application.service;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.workspace.member.domain.model.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamRepository;
import com.company.scopery.modules.workspace.workspace.domain.model.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrgMembershipAccessRevocationService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final OrgTeamMemberRepository teamMemberRepository;
    private final OrgTeamRepository teamRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final IamActivityLogger iamActivityLogger;
    private final ImmutableAuditEventService auditEventService;

    public OrgMembershipAccessRevocationService(WorkspaceRepository workspaceRepository,
                                                WorkspaceMemberRepository workspaceMemberRepository,
                                                OrgTeamMemberRepository teamMemberRepository,
                                                OrgTeamRepository teamRepository,
                                                IamAuthResourceRepository resourceRepository,
                                                IamAccessGrantRepository grantRepository,
                                                IamRoleAssignmentRepository roleAssignmentRepository,
                                                IamActivityLogger iamActivityLogger,
                                                ImmutableAuditEventService auditEventService) {
        this.workspaceRepository = workspaceRepository; this.workspaceMemberRepository = workspaceMemberRepository;
        this.teamMemberRepository = teamMemberRepository; this.teamRepository = teamRepository;
        this.resourceRepository = resourceRepository; this.grantRepository = grantRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.iamActivityLogger = iamActivityLogger;
        this.auditEventService = auditEventService;
    }

    /**
     * Cascades from an already-authorized org-level membership change (removal/suspension), so
     * each individual grant/role-assignment revoked here is logged for audit purposes but not
     * re-authorized per-resource — the caller's organization-level authorization covers the cascade.
     */
    public void revokeDescendantAccess(UUID organizationId, UUID userId, UUID actorId) {
        var workspaces = workspaceRepository.findAllActiveByOrganizationId(organizationId);
        workspaces.forEach(workspace -> workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspace.id(), userId)
                .filter(member -> member.status().name().equals("ACTIVE"))
                .ifPresent(member -> workspaceMemberRepository.save(member.deactivate())));

        teamMemberRepository.findAllByUserId(userId).stream()
                .filter(member -> teamRepository.findById(member.teamId())
                        .map(team -> team.organizationId().equals(organizationId)).orElse(false))
                .forEach(member -> teamMemberRepository.deleteByTeamIdAndUserId(member.teamId(), userId));

        resourceRepository.findAllByOrganizationId(organizationId).forEach(resource ->
                grantRepository.findActiveBySubjectsAndResource(
                                List.of(IamSubjectType.USER), List.of(userId), resource.id())
                        .forEach(grant -> revokeGrant(grant, resource, actorId)));

        roleAssignmentRepository.findActiveByAssigneeId(userId).stream()
                .filter(assignment -> assignment.workspaceId() != null)
                .filter(assignment -> workspaces.stream()
                        .anyMatch(workspace -> workspace.id().equals(assignment.workspaceId())))
                .forEach(assignment -> {
                    var saved = roleAssignmentRepository.save(assignment.deactivate());
                    iamActivityLogger.logSuccess(IamEntityTypes.IAM_ROLE_ASSIGNMENT, saved.id(),
                            IamActivityActions.DEACTIVATE_IAM_ROLE_ASSIGNMENT,
                            "Role assignment deactivated by org membership cascade: " + saved.id());
                });
    }

    private void revokeGrant(IamAccessGrant grant, IamAuthResource resource, UUID actorId) {
        IamAccessGrant saved = grantRepository.save(grant.revoke());
        iamActivityLogger.logSuccess(IamEntityTypes.IAM_ACCESS_GRANT, saved.id(),
                IamActivityActions.REVOKE_IAM_ACCESS_GRANT,
                "Access grant revoked by org membership cascade: " + saved.id());
        auditEventService.record(AuditEventType.ACCESS_GRANT_REVOKED, actorId, "USER",
                "IAM_ACCESS_GRANT", saved.id(), resource.organizationId(),
                resource.workspaceId(), grant, saved, "Access grant revoked by org membership cascade");
    }
}
