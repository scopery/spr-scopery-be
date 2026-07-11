package com.company.scopery.modules.iam.authorization.infrastructure.persistence;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationReadRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignment;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import com.company.scopery.modules.iam.user.domain.model.IamUserRepository;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import com.company.scopery.modules.workspace.team.domain.model.TeamMemberRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class JpaAuthorizationReadRepository implements AuthorizationReadRepository {

    private final IamUserRepository userRepository;
    private final IamRightRepository rightRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository permissionActionRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final OrgTeamMemberRepository orgTeamMemberRepository;

    public JpaAuthorizationReadRepository(IamUserRepository userRepository,
                                            IamRightRepository rightRepository,
                                            IamAuthResourceRepository resourceRepository,
                                            IamAccessGrantRepository grantRepository,
                                            IamAccessGrantPermissionActionRepository grantActionRepository,
                                            IamAccessGrantRightRepository grantRightRepository,
                                            IamPermissionRepository permissionRepository,
                                            IamPermissionActionDefinitionRepository permissionActionRepository,
                                            IamRoleAssignmentRepository roleAssignmentRepository,
                                            TeamMemberRepository teamMemberRepository,
                                            OrgTeamMemberRepository orgTeamMemberRepository) {
        this.userRepository = userRepository;
        this.rightRepository = rightRepository;
        this.resourceRepository = resourceRepository;
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.grantRightRepository = grantRightRepository;
        this.permissionRepository = permissionRepository;
        this.permissionActionRepository = permissionActionRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.orgTeamMemberRepository = orgTeamMemberRepository;
    }

    @Override
    public Optional<IamUser> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<IamRight> findRightByCode(IamRightCode code) {
        return rightRepository.findByCode(code);
    }

    @Override
    public Optional<IamAuthResource> findResourceById(UUID resourceId) {
        return resourceRepository.findById(resourceId);
    }

    @Override
    public Optional<IamAuthResource> findResourceByRefIdAndType(UUID refId, IamResourceType resourceType) {
        return resourceRepository.findByRefIdAndResourceType(refId, resourceType);
    }

    @Override
    public List<IamAccessGrant> findActiveGrantsBySubjectsAndResource(
            List<IamSubjectType> subjectTypes, List<UUID> subjectIds, UUID resourceId) {
        return grantRepository.findActiveBySubjectsAndResource(subjectTypes, subjectIds, resourceId);
    }

    @Override
    public Set<UUID> findGrantIdsHavingAnyPermissionAction(List<UUID> grantIds, List<UUID> permissionActionIds) {
        return grantActionRepository.findGrantIdsHavingAnyPermissionAction(grantIds, permissionActionIds);
    }

    @Override
    public Set<UUID> findGrantIdsHavingRight(List<UUID> grantIds, UUID rightId) {
        return grantRightRepository.findGrantIdsHavingRight(grantIds, rightId);
    }

    @Override
    public Optional<IamPermission> findPermissionByCode(IamPermissionCode code) {
        return permissionRepository.findByCode(code);
    }

    @Override
    public Optional<IamPermissionActionDefinition> findPermissionActionByPermissionIdAndCode(
            UUID permissionId, String actionCode) {
        return permissionActionRepository.findByPermissionIdAndActionCode(permissionId, actionCode);
    }

    @Override
    public List<IamPermissionActionDefinition> findPermissionActionsByRightIds(List<UUID> rightIds) {
        return permissionActionRepository.findByRightIds(rightIds);
    }

    @Override
    public Optional<IamPermission> findPermissionById(UUID permissionId) {
        return permissionRepository.findById(permissionId);
    }

    @Override
    public List<IamRoleAssignment> findActiveRoleAssignmentsByAssigneeId(UUID assigneeId) {
        return roleAssignmentRepository.findActiveByAssigneeId(assigneeId);
    }

    @Override
    public List<UUID> findWorkspaceTeamIdsByUserId(UUID userId) {
        return teamMemberRepository.findByUserId(userId).stream()
                .map(member -> member.teamId())
                .toList();
    }

    @Override
    public List<UUID> findOrgTeamIdsByUserId(UUID userId) {
        return orgTeamMemberRepository.findAllByUserId(userId).stream()
                .map(member -> member.teamId())
                .toList();
    }
}
