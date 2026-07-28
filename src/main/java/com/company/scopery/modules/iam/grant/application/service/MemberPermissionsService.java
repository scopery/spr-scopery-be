package com.company.scopery.modules.iam.grant.application.service;

import com.company.scopery.modules.iam.authorization.application.service.CurrentUserAuthorizationService;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionCatalogItem;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionCatalogResponse;
import com.company.scopery.modules.iam.grant.application.response.MemberPermissionsSnapshotResponse;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantEffect;
import com.company.scopery.modules.iam.grant.domain.enums.IamGrantKind;
import com.company.scopery.modules.iam.grant.domain.enums.IamSubjectType;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrant;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionAction;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantPermissionActionRepository;
import com.company.scopery.modules.iam.grant.domain.model.IamAccessGrantRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.resource.domain.enums.IamResourceType;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResource;
import com.company.scopery.modules.iam.resource.domain.model.IamAuthResourceRepository;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.roleassignment.domain.model.IamRoleAssignmentRepository;
import com.company.scopery.modules.iam.shared.constant.IamActionCodes;
import com.company.scopery.modules.iam.shared.constant.IamPermissionCodes;
import com.company.scopery.modules.iam.shared.error.IamExceptions;
import com.company.scopery.modules.workspace.orgteam.domain.model.OrgTeamMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Owner-facing member permission catalog + sync.
 * <p>
 * Catalog is filtered by resource surface so Workspace / Project / Org UIs do not show
 * overlapping delivery rights. Baseline actions appear locked; only non-baseline grantable
 * actions are synced on replace.
 */
@Service
public class MemberPermissionsService {

    private record BaselineAction(String permissionCode, String actionCode) {}

    /** Mirrors EnsureWorkspaceMemberBaselineAccessAction — do not expose in owner tick UI. */
    private static final List<BaselineAction> WORKSPACE_BASELINE_ACTIONS = List.of(
            new BaselineAction(IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.MANAGE),
            new BaselineAction(IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.DASHBOARD_VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_VIEW)
    );

    /** Mirrors EnsureProjectMemberBaselineAccessAction — do not expose as editable in owner tick UI. */
    private static final List<BaselineAction> PROJECT_BASELINE_ACTIONS = List.of(
            new BaselineAction(IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.UPDATE),
            new BaselineAction(IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.CREATE),
            new BaselineAction(IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.DASHBOARD_VIEW),
            new BaselineAction(IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_VIEW)
    );

    /**
     * Workspace member-permissions UI: shell / workspace admin only.
     * Project delivery rights are granted on PROJECT resources instead.
     */
    private static final Set<String> WORKSPACE_MEMBER_PERMISSION_SURFACE = Set.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT,
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT,
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT,
            IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT,
            IamPermissionCodes.TEAM_MANAGEMENT,
            IamPermissionCodes.TEAM_MEMBER_MANAGEMENT,
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT,
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT,
            IamPermissionCodes.KNOWLEDGE_CLASSIFICATION_MANAGEMENT,
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT,
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT,
            IamPermissionCodes.PROJECT_MANAGEMENT,
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT,
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT,
            IamPermissionCodes.CAPACITY_MANAGEMENT,
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT,
            IamPermissionCodes.EXTERNAL_PARTY_MANAGEMENT,
            IamPermissionCodes.CLIENT_PORTAL_MANAGEMENT
    );

    /**
     * Project member-permissions UI: delivery / commercial / quality / control on that project.
     */
    private static final Set<String> PROJECT_MEMBER_PERMISSION_SURFACE = Set.of(
            IamPermissionCodes.PROJECT_MANAGEMENT,
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT,
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT,
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT,
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT,
            IamPermissionCodes.SCOPE_MANAGEMENT,
            IamPermissionCodes.DELIVERABLE_MANAGEMENT,
            IamPermissionCodes.REQUIREMENT_MANAGEMENT,
            IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT,
            IamPermissionCodes.RAID_MANAGEMENT,
            IamPermissionCodes.DECISION_MANAGEMENT,
            IamPermissionCodes.COLLABORATION_MANAGEMENT,
            IamPermissionCodes.COMMENT_MANAGEMENT,
            IamPermissionCodes.REPORTING_MANAGEMENT,
            IamPermissionCodes.ESTIMATION_MANAGEMENT,
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT,
            IamPermissionCodes.QUOTE_MANAGEMENT,
            IamPermissionCodes.PROFITABILITY_MANAGEMENT,
            IamPermissionCodes.QUALITY_MANAGEMENT,
            IamPermissionCodes.TEST_MANAGEMENT,
            IamPermissionCodes.DEFECT_MANAGEMENT,
            IamPermissionCodes.RELEASE_MANAGEMENT,
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT,
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT,
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT,
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT
    );

    private static final Set<String> ORGANIZATION_MEMBER_PERMISSION_SURFACE = Set.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT,
            IamPermissionCodes.TEAM_MANAGEMENT,
            IamPermissionCodes.TEAM_MEMBER_MANAGEMENT
    );

    private final IamAuthResourceRepository authResourceRepository;
    private final IamAccessGrantRepository grantRepository;
    private final IamAccessGrantPermissionActionRepository grantActionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamPermissionRepository permissionRepository;
    private final IamRightRepository rightRepository;
    private final OrgTeamMemberRepository teamMemberRepository;
    private final IamRoleAssignmentRepository roleAssignmentRepository;
    private final CurrentUserAuthorizationService currentUserService;

    public MemberPermissionsService(
            IamAuthResourceRepository authResourceRepository,
            IamAccessGrantRepository grantRepository,
            IamAccessGrantPermissionActionRepository grantActionRepository,
            IamPermissionActionDefinitionRepository actionRepository,
            IamPermissionRepository permissionRepository,
            IamRightRepository rightRepository,
            OrgTeamMemberRepository teamMemberRepository,
            IamRoleAssignmentRepository roleAssignmentRepository,
            CurrentUserAuthorizationService currentUserService) {
        this.authResourceRepository = authResourceRepository;
        this.grantRepository = grantRepository;
        this.grantActionRepository = grantActionRepository;
        this.actionRepository = actionRepository;
        this.permissionRepository = permissionRepository;
        this.rightRepository = rightRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.roleAssignmentRepository = roleAssignmentRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public MemberPermissionCatalogResponse catalog(IamResourceType resourceType, UUID resourceRefId) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamAuthResource resource = requireResource(resourceType, resourceRefId);
        Set<UUID> baselineIds = filterToMemberPermissionSurface(
                resourceType, resolveBaselineActionIds(resourceType));
        Set<UUID> grantable = filterToMemberPermissionSurface(
                resourceType, grantableActionIds(actorId, resource));
        grantable.removeAll(baselineIds);

        Map<UUID, Set<UUID>> holdersByAction = new HashMap<>();
        for (IamAccessGrant grant : grantRepository.findActiveByResource(resource.id())) {
            if (grant.subjectType() != IamSubjectType.USER || grant.effect() != IamGrantEffect.ALLOW) {
                continue;
            }
            for (IamAccessGrantPermissionAction link : grantActionRepository.findByGrantId(grant.id())) {
                UUID actionId = link.permissionActionId();
                if (!grantable.contains(actionId) && !baselineIds.contains(actionId)) continue;
                holdersByAction
                        .computeIfAbsent(actionId, id -> new HashSet<>())
                        .add(grant.subjectId());
            }
        }

        List<MemberPermissionCatalogItem> items = new ArrayList<>();
        for (UUID actionId : grantable) {
            MemberPermissionCatalogItem item = toCatalogItem(actionId, holdersByAction, false);
            if (item != null) items.add(item);
        }
        for (UUID actionId : baselineIds) {
            if (grantable.contains(actionId)) continue;
            MemberPermissionCatalogItem item = toCatalogItem(actionId, holdersByAction, true);
            if (item != null) items.add(item);
        }

        items.sort((a, b) -> {
            if (a.baseline() != b.baseline()) return a.baseline() ? 1 : -1;
            int m = Objects.toString(a.module(), "").compareToIgnoreCase(Objects.toString(b.module(), ""));
            if (m != 0) return m;
            return a.title().compareToIgnoreCase(b.title());
        });

        return new MemberPermissionCatalogResponse(resourceType.name(), resourceRefId, items);
    }

    private MemberPermissionCatalogItem toCatalogItem(
            UUID actionId,
            Map<UUID, Set<UUID>> holdersByAction,
            boolean baseline) {
        IamPermissionActionDefinition action = actionRepository.findById(actionId).orElse(null);
        if (action == null) return null;
        IamRight right = action.rightId() == null
                ? null
                : rightRepository.findById(action.rightId()).orElse(null);
        String title = right != null && right.name() != null && !right.name().isBlank()
                ? right.name()
                : (action.name() != null ? action.name() : "Permission");
        String description = right != null && right.description() != null && !right.description().isBlank()
                ? right.description()
                : (action.description() != null ? action.description() : "");
        String module = right != null ? right.module() : "";
        List<UUID> holders = holdersByAction.getOrDefault(actionId, Set.of()).stream()
                .sorted()
                .toList();
        return new MemberPermissionCatalogItem(actionId, title, description, module, holders, baseline);
    }

    @Transactional(readOnly = true)
    public MemberPermissionsSnapshotResponse snapshotForUser(
            IamResourceType resourceType, UUID resourceRefId, UUID userId) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamAuthResource resource = requireResource(resourceType, resourceRefId);
        Set<UUID> baselineIds = filterToMemberPermissionSurface(
                resourceType, resolveBaselineActionIds(resourceType));
        Set<UUID> grantable = filterToMemberPermissionSurface(
                resourceType, grantableActionIds(actorId, resource));
        grantable.removeAll(baselineIds);

        Set<UUID> held = memberGrantableActions(userId, resource, grantable);
        return new MemberPermissionsSnapshotResponse(userId, held.stream().sorted().toList());
    }

    @Transactional
    public MemberPermissionsSnapshotResponse replaceForUser(
            IamResourceType resourceType,
            UUID resourceRefId,
            UUID userId,
            List<UUID> desiredActionIds) {
        UUID actorId = currentUserService.resolveCurrentUser().id();
        IamAuthResource resource = requireResource(resourceType, resourceRefId);
        Set<UUID> baselineIds = filterToMemberPermissionSurface(
                resourceType, resolveBaselineActionIds(resourceType));
        Set<UUID> grantable = filterToMemberPermissionSurface(
                resourceType, grantableActionIds(actorId, resource));
        grantable.removeAll(baselineIds);

        Set<UUID> desired = new HashSet<>();
        if (desiredActionIds != null) {
            for (UUID id : desiredActionIds) {
                if (id == null) continue;
                if (baselineIds.contains(id)) continue;
                if (!grantable.contains(id)) {
                    throw IamExceptions.iamDelegationNotPermitted(actorId, resource.id());
                }
                desired.add(id);
            }
        }

        IamAccessGrant memberGrant = ensureMemberAllowGrant(resource, userId, actorId);
        Set<UUID> current = memberGrantableActions(userId, resource, grantable);

        Set<UUID> toAdd = new HashSet<>(desired);
        toAdd.removeAll(current);
        Set<UUID> toRemove = new HashSet<>(current);
        toRemove.removeAll(desired);

        for (UUID actionId : toAdd) {
            if (!grantActionRepository.existsByGrantIdAndPermissionActionId(memberGrant.id(), actionId)) {
                grantActionRepository.save(IamAccessGrantPermissionAction.create(memberGrant.id(), actionId));
            }
        }
        for (UUID actionId : toRemove) {
            grantActionRepository.deleteByGrantIdAndPermissionActionId(memberGrant.id(), actionId);
        }

        return new MemberPermissionsSnapshotResponse(userId, desired.stream().sorted().toList());
    }

    private IamAuthResource requireResource(IamResourceType type, UUID refId) {
        return authResourceRepository.findByRefIdAndResourceType(refId, type)
                .orElseThrow(() -> IamExceptions.iamAuthResourceNotFound(refId));
    }

    private Set<UUID> grantableActionIds(UUID actorId, IamAuthResource resource) {
        LinkedHashMap<UUID, Boolean> out = new LinkedHashMap<>();
        for (IamAccessGrant grant : actorGrants(actorId, resource)) {
            if (!grant.canDelegate()) continue;
            if (grant.effect() != IamGrantEffect.ALLOW) continue;
            for (IamAccessGrantPermissionAction link : grantActionRepository.findByGrantId(grant.id())) {
                out.put(link.permissionActionId(), Boolean.TRUE);
            }
        }
        return new HashSet<>(out.keySet());
    }

    private List<IamAccessGrant> actorGrants(UUID actorId, IamAuthResource resource) {
        List<IamSubjectType> types = new ArrayList<>(List.of(IamSubjectType.USER));
        List<UUID> ids = new ArrayList<>(List.of(actorId));
        teamMemberRepository.findAllByUserId(actorId).forEach(member -> {
            types.add(IamSubjectType.TEAM);
            ids.add(member.teamId());
        });
        roleAssignmentRepository.findActiveByAssigneeId(actorId).forEach(assignment -> {
            if (assignment.workspaceId() == null || assignment.workspaceId().equals(resource.workspaceId())) {
                types.add(IamSubjectType.ROLE);
                ids.add(assignment.roleId());
            }
        });
        return grantRepository.findActiveBySubjectsAndResource(types, ids, resource.id());
    }

    private Set<UUID> memberGrantableActions(UUID userId, IamAuthResource resource, Set<UUID> grantable) {
        return grantRepository
                .findActiveBySubjectsAndResource(List.of(IamSubjectType.USER), List.of(userId), resource.id())
                .stream()
                .filter(g -> g.effect() == IamGrantEffect.ALLOW)
                .flatMap(g -> grantActionRepository.findByGrantId(g.id()).stream())
                .map(IamAccessGrantPermissionAction::permissionActionId)
                .filter(grantable::contains)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private IamAccessGrant ensureMemberAllowGrant(IamAuthResource resource, UUID userId, UUID actorId) {
        return grantRepository
                .findActiveBySubjectsAndResource(List.of(IamSubjectType.USER), List.of(userId), resource.id())
                .stream()
                .filter(g -> g.effect() == IamGrantEffect.ALLOW)
                .findFirst()
                .orElseGet(() -> grantRepository.save(IamAccessGrant.createWithMetadata(
                        IamSubjectType.USER,
                        userId,
                        resource.id(),
                        null,
                        IamGrantEffect.ALLOW,
                        null,
                        null,
                        resource.workspaceId() != null ? resource.workspaceId() : resource.refId(),
                        IamGrantKind.DELEGATED,
                        null,
                        false,
                        0,
                        null,
                        null,
                        "Owner-delegated member permissions",
                        actorId)));
    }

    private Set<UUID> resolveBaselineActionIds(IamResourceType resourceType) {
        List<BaselineAction> baselines = resourceType == IamResourceType.PROJECT
                ? PROJECT_BASELINE_ACTIONS
                : WORKSPACE_BASELINE_ACTIONS;
        Set<UUID> ids = new HashSet<>();
        for (BaselineAction baseline : baselines) {
            IamPermission permission = permissionRepository
                    .findByCode(IamPermissionCode.of(baseline.permissionCode()))
                    .orElse(null);
            if (permission == null) continue;
            actionRepository.findByPermissionIdAndActionCode(permission.id(), baseline.actionCode())
                    .ifPresent(action -> ids.add(action.id()));
        }
        return ids;
    }

    private Set<UUID> filterToMemberPermissionSurface(IamResourceType resourceType, Set<UUID> actionIds) {
        Set<String> surface = switch (resourceType) {
            case WORKSPACE -> WORKSPACE_MEMBER_PERMISSION_SURFACE;
            case PROJECT -> PROJECT_MEMBER_PERMISSION_SURFACE;
            case ORGANIZATION -> ORGANIZATION_MEMBER_PERMISSION_SURFACE;
            default -> null;
        };
        if (surface == null || actionIds.isEmpty()) {
            return actionIds;
        }
        Set<UUID> filtered = new HashSet<>();
        for (UUID actionId : actionIds) {
            if (isActionInSurface(actionId, surface)) {
                filtered.add(actionId);
            }
        }
        return filtered;
    }

    private boolean isActionInSurface(UUID actionId, Set<String> surface) {
        IamPermissionActionDefinition action = actionRepository.findById(actionId).orElse(null);
        if (action == null) return false;
        IamPermission permission = permissionRepository.findById(action.permissionId()).orElse(null);
        if (permission == null) return false;
        return surface.contains(permission.code().value());
    }
}
