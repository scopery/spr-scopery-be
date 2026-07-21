package com.company.scopery.modules.iam.authorization.application.service;

import com.company.scopery.common.audit.AuditEventType;
import com.company.scopery.common.audit.ImmutableAuditEventService;
import com.company.scopery.modules.iam.authorization.domain.enums.AuthorizationDecisionReason;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationExplanation;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationReadRepository;
import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationRequest;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceStatus;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.right.domain.enums.IamRightStatus;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.shared.activity.IamActivityLogger;
import com.company.scopery.modules.iam.shared.constant.IamActivityActions;
import com.company.scopery.modules.iam.shared.constant.IamEntityTypes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.iam.user.domain.enums.IamUserStatus;
import com.company.scopery.modules.iam.user.domain.model.IamUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthorizationDecisionService {

    private final AuthorizationReadRepository readRepository;
    private final IamActivityLogger activityLogger;
    private final ImmutableAuditEventService auditEventService;

    public AuthorizationDecisionService(AuthorizationReadRepository readRepository,
                                         IamActivityLogger activityLogger,
                                         ImmutableAuditEventService auditEventService) {
        this.readRepository = readRepository;
        this.activityLogger = activityLogger;
        this.auditEventService = auditEventService;
    }

    @Transactional(readOnly = true)
    public AuthorizationDecision canAccess(AuthorizationRequest request) {
        IamUser user = readRepository.findUserById(request.userId()).orElse(null);
        if (user == null) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.USER_NOT_FOUND);
        }
        if (user.status() != IamUserStatus.ACTIVE) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.USER_INACTIVE);
        }

        boolean hasGlobalGrant = readRepository.hasActiveGlobalResourceGrantForUser(request.userId());
        if (hasGlobalGrant) {
            return AuthorizationDecision.allow(AuthorizationDecisionReason.GLOBAL_RESOURCE_GRANT_ALLOW);
        }

        ResolvedAuthority authority = resolveAuthority(request);
        if (authority.denyReason() != null) {
            return AuthorizationDecision.deny(authority.denyReason());
        }

        IamAuthResource resource = readRepository.findResourceById(request.resourceId()).orElse(null);
        if (resource == null) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RESOURCE_NOT_FOUND);
        }
        if (resource.status() != IamResourceStatus.ACTIVE) {
            return AuthorizationDecision.deny(AuthorizationDecisionReason.RESOURCE_INACTIVE);
        }

        List<IamAccessGrant> allGrants = collectAllGrants(request.userId(), request.resourceId());

        if (!allGrants.isEmpty()) {
            List<UUID> grantIds = allGrants.stream().map(IamAccessGrant::id).toList();
            Set<UUID> grantIdsWithAuthority = findGrantIdsHavingAuthority(grantIds, authority);
            for (IamAccessGrant grant : allGrants) {
                if (grant.effect() == IamGrantEffect.DENY && coversAuthority(grant.id(), grantIdsWithAuthority)) {
                    return AuthorizationDecision.deny(AuthorizationDecisionReason.EXPLICIT_DENY);
                }
            }

            for (IamAccessGrant grant : allGrants) {
                if (grant.effect() == IamGrantEffect.ALLOW && coversAuthority(grant.id(), grantIdsWithAuthority)) {
                    return AuthorizationDecision.allow(resolveAllowReason(grant.subjectType()));
                }
            }
        }

        return AuthorizationDecision.deny(AuthorizationDecisionReason.DEFAULT_DENY);
    }

    public void requireAccess(AuthorizationRequest request) {
        AuthorizationDecision decision = canAccess(request);

        activityLogger.logSuccess(IamEntityTypes.IAM_AUTHORIZATION_DECISION, request.resourceId(),
                IamActivityActions.CHECK_AUTHORIZATION,
                "Authorization check for user " + request.userId()
                        + " authority " + request.authorityLabel()
                        + " resource " + request.resourceId()
                        + " → " + (decision.allowed() ? "ALLOW" : "DENY") + " (" + decision.reason() + ")");

        if (!decision.allowed()) {
            IamAuthResource resource = readRepository.findResourceById(request.resourceId()).orElse(null);
            auditEventService.record(AuditEventType.IAM_AUTHORIZATION_DENIED, request.userId(), "USER",
                    resource == null ? "IAM_RESOURCE" : resource.resourceType().name(),
                    resource == null ? request.resourceId() : resource.refId(),
                    resource == null ? null : resource.organizationId(),
                    resource == null ? null : resource.workspaceId(), null, null,
                    request.authorityLabel() + ": " + decision.reason());
            throw IamExceptions.accessDenied(
                    request.userId(), "resource", request.resourceId(), request.authorityLabel());
        }
    }

    @Transactional(readOnly = true)
    public AuthorizationExplanation explainAccess(AuthorizationRequest request) {
        AuthorizationDecision decision = canAccess(request);
        List<UUID> grantIds = findContributingGrantIds(request, decision);
        String explanation = decision.allowed()
                ? "Allowed by " + decision.reason().name()
                : "Denied by " + decision.reason().name();
        return new AuthorizationExplanation(decision.allowed(), decision.reason().name(), grantIds, explanation);
    }

    private List<UUID> findContributingGrantIds(AuthorizationRequest request, AuthorizationDecision decision) {
        ResolvedAuthority authority = resolveAuthority(request);
        if (authority.denyReason() != null) return List.of();
        List<IamAccessGrant> grants = collectAllGrants(request.userId(), request.resourceId());
        if (grants.isEmpty()) return List.of();
        Set<UUID> matching = findGrantIdsHavingAuthority(grants.stream().map(IamAccessGrant::id).toList(), authority);
        return grants.stream()
                .filter(grant -> matching.contains(grant.id()))
                .filter(grant -> decision.allowed()
                        ? grant.effect() == IamGrantEffect.ALLOW
                        : grant.effect() == IamGrantEffect.DENY)
                .map(IamAccessGrant::id)
                .toList();
    }

    private List<IamAccessGrant> collectAllGrants(UUID userId, UUID resourceId) {
        List<IamSubjectType> subjectTypes = new ArrayList<>();
        List<UUID> subjectIds = new ArrayList<>();

        subjectTypes.add(IamSubjectType.USER);
        subjectIds.add(userId);

        readRepository.findWorkspaceTeamIdsByUserId(userId).forEach(teamId -> {
            subjectTypes.add(IamSubjectType.TEAM);
            subjectIds.add(teamId);
        });

        readRepository.findOrgTeamIdsByUserId(userId).forEach(teamId -> {
            if (!subjectIds.contains(teamId)) {
                subjectTypes.add(IamSubjectType.TEAM);
                subjectIds.add(teamId);
            }
        });

        readRepository.findActiveRoleAssignmentsByAssigneeId(userId).forEach(assignment -> {
            subjectTypes.add(IamSubjectType.ROLE);
            subjectIds.add(assignment.roleId());
        });

        if (subjectIds.isEmpty()) return List.of();
        return readRepository.findActiveGrantsBySubjectsAndResource(subjectTypes, subjectIds, resourceId);
    }

    private Set<UUID> findGrantIdsHavingAuthority(List<UUID> grantIds, ResolvedAuthority authority) {
        if (!authority.permissionActionIds().isEmpty()) {
            return readRepository.findGrantIdsHavingAnyPermissionAction(grantIds, authority.permissionActionIds());
        }
        if (authority.legacyRightId() != null) {
            return readRepository.findGrantIdsHavingRight(grantIds, authority.legacyRightId());
        }
        return Set.of();
    }

    private boolean coversAuthority(UUID grantId, Set<UUID> grantIdsWithAuthority) {
        return grantIdsWithAuthority.contains(grantId);
    }

    private AuthorizationDecisionReason resolveAllowReason(IamSubjectType subjectType) {
        return switch (subjectType) {
            case USER -> AuthorizationDecisionReason.USER_GRANT_ALLOW;
            case TEAM -> AuthorizationDecisionReason.TEAM_GRANT_ALLOW;
            case ROLE -> AuthorizationDecisionReason.ROLE_GRANT_ALLOW;
        };
    }

    private ResolvedAuthority resolveAuthority(AuthorizationRequest request) {
        if (request.permissionCode() != null && request.actionCode() != null) {
            return resolvePermissionActionAuthority(request.permissionCode(), request.actionCode());
        }
        return resolveLegacyRightAuthority(request.rightCode());
    }

    private ResolvedAuthority resolvePermissionActionAuthority(String permissionCode, String actionCode) {
        IamPermission permission;
        try {
            permission = readRepository.findPermissionByCode(IamPermissionCode.of(permissionCode)).orElse(null);
        } catch (IllegalArgumentException e) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.PERMISSION_ACTION_NOT_FOUND);
        }
        if (permission == null) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.PERMISSION_ACTION_NOT_FOUND);
        }
        if (permission.status() != IamPermissionStatus.ACTIVE) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.PERMISSION_ACTION_INACTIVE);
        }

        IamPermissionActionDefinition action = readRepository
                .findPermissionActionByPermissionIdAndCode(permission.id(), actionCode.trim().toUpperCase())
                .orElse(null);
        if (action == null) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.PERMISSION_ACTION_NOT_FOUND);
        }
        if (action.status() != IamPermissionStatus.ACTIVE) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.PERMISSION_ACTION_INACTIVE);
        }
        return ResolvedAuthority.permissionActions(List.of(action.id()));
    }

    private ResolvedAuthority resolveLegacyRightAuthority(String rightCode) {
        IamRight right = readRepository.findRightByCode(new IamRightCode(rightCode)).orElse(null);
        if (right == null || right.status() != IamRightStatus.ACTIVE) {
            return ResolvedAuthority.deny(AuthorizationDecisionReason.RIGHT_NOT_FOUND);
        }

        List<IamPermissionActionDefinition> mappedActions = readRepository.findPermissionActionsByRightIds(List.of(right.id()));
        if (mappedActions == null) {
            mappedActions = List.of();
        }
        List<UUID> mappedActionIds = mappedActions.stream()
                .filter(action -> action.status() == IamPermissionStatus.ACTIVE)
                .filter(this::permissionIsActive)
                .map(IamPermissionActionDefinition::id)
                .toList();
        if (!mappedActionIds.isEmpty()) {
            return ResolvedAuthority.permissionActions(mappedActionIds);
        }
        return ResolvedAuthority.legacyRight(right.id());
    }

    private boolean permissionIsActive(IamPermissionActionDefinition action) {
        return readRepository.findPermissionById(action.permissionId())
                .map(permission -> permission.status() == IamPermissionStatus.ACTIVE)
                .orElse(false);
    }

    private record ResolvedAuthority(
            List<UUID> permissionActionIds,
            UUID legacyRightId,
            AuthorizationDecisionReason denyReason) {

        static ResolvedAuthority permissionActions(List<UUID> permissionActionIds) {
            return new ResolvedAuthority(permissionActionIds, null, null);
        }

        static ResolvedAuthority legacyRight(UUID legacyRightId) {
            return new ResolvedAuthority(List.of(), legacyRightId, null);
        }

        static ResolvedAuthority deny(AuthorizationDecisionReason reason) {
            return new ResolvedAuthority(List.of(), null, reason);
        }
    }
}
