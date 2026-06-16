package com.company.scopery.modules.iam.authorization.application;

import com.company.scopery.modules.iam.authorization.domain.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationDecisionReason;
import com.company.scopery.modules.iam.authorization.domain.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRightRepository;
import com.company.scopery.modules.iam.grant.domain.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.IamAccessGrantRepository;
import com.company.scopery.modules.iam.grant.domain.IamSubjectType;
import com.company.scopery.modules.iam.resource.domain.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.IamAuthResourceRepository;
import com.company.scopery.modules.iam.resource.domain.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.IamResourceVisibility;
import com.company.scopery.modules.iam.right.domain.IamRight;
import com.company.scopery.modules.iam.right.domain.IamRightCode;
import com.company.scopery.modules.iam.right.domain.IamRightRepository;
import com.company.scopery.modules.iam.right.domain.IamRightStatus;
import com.company.scopery.modules.iam.roleassignment.domain.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.domain.IamUser;
import com.company.scopery.modules.iam.user.domain.IamUserRepository;
import com.company.scopery.modules.iam.user.domain.IamUserStatus;
import com.company.scopery.modules.workspace.member.domain.WorkspaceMemberRepository;
import com.company.scopery.modules.workspace.team.domain.TeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorizationDecisionService {

    // Owner-implicit rights: owning a resource grants these without explicit grants
    private static final Set<String> OWNER_IMPLICIT_RIGHTS = Set.of(
            "VIEW", "UPDATE", "DELETE", "SHARE", "MANAGE_PERMISSION");

    private final IamUserRepository userRepository;
    private final IamRightRepository rightRepository;
    private final IamAuthResourceRepository resourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantRightRepository grantRightRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final IamActivityLogger activityLogger;

    public AuthorizationDecisionService(IamUserRepository userRepository,
                                         IamRightRepository rightRepository,
                                         IamAuthResourceRepository resourceRepository,
                                         IamAccessGrantRepository grantRepository,
                                         IamAccessGrantRightRepository grantRightRepository,
                                         IamRoleAssignmentRepository roleAssignmentRepository,
                                         TeamMemberRepository teamMemberRepository,
                                         WorkspaceMemberRepository workspaceMemberRepository,
                                         IamActivityLogger activityLogger) {
        this.userRepository = userRepository;
        this.rightRepository = rightRepository;
        this.resourceRepository = resourceRepository;
        this.grantRepository = grantRepository;
        this.grantRightRepository = grantRightRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.activityLogger = activityLogger;
    }

    @Transactional(readOnly = true)
    public AuthorizationDecision canAccess(AuthorizationRequest request) {
        // 1. Validate user
        IamUser user = userRepository.findById(request.userId()).orElse(null);
        if (user == null) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.USER_NOT_FOUND);
        }
        if (user.status() != IamUserStatus.ACTIVE) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.USER_INACTIVE);
        }

        // 2. Validate right
        IamRight right = rightRepository.findByCode(new IamRightCode(request.rightCode())).orElse(null);
        if (right == null) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
        }
        if (right.status() != IamRightStatus.ACTIVE) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
        }

        // 3. Load resource
        IamAuthResource resource = resourceRepository.findById(request.resourceId()).orElse(null);
        if (resource == null) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RESOURCE_NOT_FOUND);
        }
        if (resource.status() != IamResourceStatus.ACTIVE) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RESOURCE_INACTIVE);
        }

        // 4. Owner implicit rights
        if (resource.isOwnedBy(request.userId())
                && OWNER_IMPLICIT_RIGHTS.contains(request.rightCode().toUpperCase())) {
            return AuthorizationDecision.allow(AuthorizationDecisionReason.OWNER_IMPLICIT_ALLOW);
        }

        // 5. Collect all active grants for user (direct USER + TEAM + ROLE)
        List<IamAccessGrant> allGrants = collectAllGrants(request.userId(), request.resourceId());

        if (!allGrants.isEmpty()) {
            List<UUID> grantIds = allGrants.stream().map(IamAccessGrant::id).toList();
            Set<UUID> grantIdsWithThisRight = grantRightRepository.findGrantIdsHavingRight(grantIds, right.id());
            Set<UUID> grantIdsWithAnyRight  = grantRightRepository.findGrantIdsHavingAnyRight(grantIds);

            // 5a. DENY wins — check DENY grants first
            for (IamAccessGrant grant : allGrants) {
                if (grant.effect() == IamGrantEffect.DENY && coversRight(grant.id(), grantIdsWithThisRight, grantIdsWithAnyRight)) {
                    return AuthorizationDecision.deny(AuthorizationDecisionReason.EXPLICIT_DENY);
                }
            }

            // 5b. Check ALLOW grants
            for (IamAccessGrant grant : allGrants) {
                if (grant.effect() == IamGrantEffect.ALLOW && coversRight(grant.id(), grantIdsWithThisRight, grantIdsWithAnyRight)) {
                    return AuthorizationDecision.allow(resolveAllowReason(grant.subjectType()));
                }
            }
        }

        // 6. Visibility fallback — WORKSPACE visibility allows VIEW for workspace members
        if (resource.visibility() == IamResourceVisibility.WORKSPACE
                && resource.workspaceId() != null
                && "VIEW".equalsIgnoreCase(request.rightCode())
                && workspaceMemberRepository.isActiveMember(resource.workspaceId(), request.userId())) {
            return AuthorizationDecision.allow(AuthorizationDecisionReason.VISIBILITY_ALLOW);
        }

        // 7. Default deny
        return AuthorizationDecision.deny(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    public void requireAccess(AuthorizationRequest request) {
        AuthorizationDecision decision = canAccess(request);

        activityLogger.logSuccess(IamEntityTypes.IAM_AUTHORIZATION_DECISION, request.resourceId(),
                IamActivityActions.CHECK_AUTHORIZATION,
                "Authorization check for user " + request.userId()
                        + " right " + request.rightCode()
                        + " resource " + request.resourceId()
                        + " → " + (decision.allowed() ? "ALLOW" : "DENY") + " (" + decision.reason() + ")");

        if (!decision.allowed()) {
            throw IamExceptions.accessDenied(
                    request.userId(), "resource", request.resourceId(), request.rightCode());
        }
    }

    private List<IamAccessGrant> collectAllGrants(UUID userId, UUID resourceId) {
        List<IamSubjectType> subjectTypes = new ArrayList<>();
        List<UUID> subjectIds = new ArrayList<>();

        // USER direct
        subjectTypes.add(IamSubjectType.USER);
        subjectIds.add(userId);

        // TEAM memberships
        teamMemberRepository.findByUserId(userId).forEach(m -> {
            subjectTypes.add(IamSubjectType.TEAM);
            subjectIds.add(m.teamId());
        });

        // ROLE assignments
        roleAssignmentRepository.findActiveByAssigneeId(userId).forEach(a -> {
            subjectTypes.add(IamSubjectType.ROLE);
            subjectIds.add(a.roleId());
        });

        if (subjectIds.isEmpty()) return List.of();
        return grantRepository.findActiveBySubjectsAndResource(subjectTypes, subjectIds, resourceId);
    }

    private boolean coversRight(UUID grantId, Set<UUID> grantIdsWithThisRight, Set<UUID> grantIdsWithAnyRight) {
        // Blanket grant (no attached rights) OR explicitly has the right
        return !grantIdsWithAnyRight.contains(grantId) || grantIdsWithThisRight.contains(grantId);
    }

    private AuthorizationDecisionReason resolveAllowReason(IamSubjectType subjectType) {
        return switch (subjectType) {
            case USER -> AuthorizationDecisionReason.USER_GRANT_ALLOW;
            case TEAM -> AuthorizationDecisionReason.TEAM_GRANT_ALLOW;
            case ROLE -> AuthorizationDecisionReason.ROLE_GRANT_ALLOW;
        };
    }
}
