package com.company.scopery.modules.iam.permission.application.listeners;

import com.company.scopery.modules.iam.permission.domain.enums.IamDataAccessPolicy;
import com.company.scopery.modules.iam.permission.domain.model.IamPermission;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinition;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionActionDefinitionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionAssignableSubjectType;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionCategory;
import com.company.scopery.modules.iam.permission.domain.valueobject.IamPermissionCode;
import com.company.scopery.modules.iam.permission.domain.model.IamPermissionRepository;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionRiskLevel;
import com.company.scopery.modules.iam.permission.domain.enums.IamPermissionStatus;
import com.company.scopery.modules.iam.permission.domain.enums.IamResourceScopeLevel;
import com.company.scopery.modules.iam.right.domain.model.IamRight;
import com.company.scopery.modules.iam.right.domain.valueobject.IamRightCode;
import com.company.scopery.modules.iam.right.domain.model.IamRightRepository;
import com.company.scopery.modules.iam.shared.constant.IamActionCodes;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.constant.IamPermissionCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Order(200)
public class IamPermissionCatalogInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(IamPermissionCatalogInitializer.class);
    private static final Set<IamPermissionAssignableSubjectType> SYSTEM_ASSIGNABLE_SUBJECTS =
            Set.of(IamPermissionAssignableSubjectType.USER, IamPermissionAssignableSubjectType.ROLE);
    private static final Set<IamPermissionAssignableSubjectType> ORGANIZATION_ASSIGNABLE_SUBJECTS =
            Set.of(IamPermissionAssignableSubjectType.USER, IamPermissionAssignableSubjectType.ROLE);
    private static final Set<IamPermissionAssignableSubjectType> WORKSPACE_ASSIGNABLE_SUBJECTS =
            Set.of(IamPermissionAssignableSubjectType.USER, IamPermissionAssignableSubjectType.TEAM,
                    IamPermissionAssignableSubjectType.ROLE);

    private final IamPermissionRepository permissionRepository;
    private final IamPermissionActionDefinitionRepository actionRepository;
    private final IamRightRepository rightRepository;

    public IamPermissionCatalogInitializer(IamPermissionRepository permissionRepository,
                                           IamPermissionActionDefinitionRepository actionRepository,
                                           IamRightRepository rightRepository) {
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.rightRepository = rightRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, PermissionDef> permissionDefs = buildPermissionDefs().stream()
                .collect(Collectors.toMap(PermissionDef::code, Function.identity()));

        int permissionsSeeded = 0;
        int actionsSeeded = 0;
        int actionsUpdated = 0;
        int actionsDeactivated = 0;

        for (PermissionDef def : permissionDefs.values()) {
            Optional<IamPermission> existingPermission = permissionRepository.findByCode(IamPermissionCode.of(def.code()));
            IamPermission permission;
            if (existingPermission.isPresent()) {
                permission = existingPermission.get();
                if (needsPermissionSync(permission, def)) {
                    permission = permissionRepository.save(permission.syncCatalog(
                            def.moduleCode(), def.name(), def.description(),
                            def.resourceScopeLevel(), def.dataAccessPolicy(),
                            def.permissionCategory(), def.assignableSubjectTypes(), def.riskLevel()));
                }
            } else {
                permission = permissionRepository.save(IamPermission.create(
                        IamPermissionCode.of(def.code()),
                        def.moduleCode(),
                        def.name(),
                        def.description(),
                        def.resourceScopeLevel(),
                        def.dataAccessPolicy(),
                        def.permissionCategory(),
                        def.assignableSubjectTypes(),
                        def.riskLevel()));
                permissionsSeeded++;
            }

            List<IamPermissionAction> actions = IamAuthorities.all().stream()
                    .filter(a -> a.permissionCode().equals(def.code()))
                    .toList();
            Set<String> desiredActionCodes = actions.stream()
                    .map(IamPermissionAction::actionCode)
                    .collect(Collectors.toSet());
            for (IamPermissionAction action : actions) {
                UUID rightId = resolveRightId(action.legacyRightCode());
                String actionName = toTitle(action.actionCode());
                String actionDescription = "Allows " + action.actionCode().toLowerCase().replace('_', ' ')
                        + " for " + def.name();
                Optional<IamPermissionActionDefinition> existing =
                        actionRepository.findByPermissionIdAndActionCode(permission.id(), action.actionCode());
                if (existing.isEmpty()) {
                    actionRepository.save(IamPermissionActionDefinition.create(
                            permission.id(),
                            action.actionCode(),
                            actionName,
                            actionDescription,
                            rightId));
                    actionsSeeded++;
                } else if (needsActionSync(existing.get(), actionName, actionDescription, rightId)) {
                    actionRepository.save(existing.get().syncCatalog(actionName, actionDescription, rightId));
                    actionsUpdated++;
                }
            }

            for (IamPermissionActionDefinition existingAction : actionRepository.findByPermissionId(permission.id())) {
                if (!desiredActionCodes.contains(existingAction.actionCode())
                        && existingAction.status() == IamPermissionStatus.ACTIVE) {
                    actionRepository.save(existingAction.deactivate());
                    actionsDeactivated++;
                }
            }
        }

        int permissionsDeactivated = deactivateObsoletePermissions();

        if (permissionsSeeded > 0 || actionsSeeded > 0 || actionsUpdated > 0
                || actionsDeactivated > 0 || permissionsDeactivated > 0) {
            log.info("IAM permission catalog: seeded {} permissions, seeded {} actions, updated {} actions, "
                            + "deactivated {} actions, deactivated {} permissions",
                    permissionsSeeded, actionsSeeded, actionsUpdated, actionsDeactivated, permissionsDeactivated);
        }
    }

    private UUID resolveRightId(String rightCode) {
        return rightRepository.findByCode(IamRightCode.of(rightCode))
                .map(IamRight::id)
                .orElseGet(() -> {
                    log.warn("IAM permission catalog: backing right not found: {}", rightCode);
                    return null;
                });
    }

    private List<PermissionDef> buildPermissionDefs() {
        return List.of(
                new PermissionDef(IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, "IAM",
                        "System IAM Management",
                        "Manage system-level IAM users, roles, rights, and grants",
                        IamResourceScopeLevel.SYSTEM, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.SECURITY, SYSTEM_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.CRITICAL),
                new PermissionDef(IamPermissionCodes.SYSTEM_RESOURCE_MANAGEMENT, "IAM",
                        "System Resource Management",
                        "Manage IAM protected resource registry",
                        IamResourceScopeLevel.SYSTEM, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.RESOURCE_ADMIN, SYSTEM_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.CRITICAL),
                new PermissionDef(IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, "GOVERNANCE",
                        "System Governance Management",
                        "Manage system governance roles and configuration",
                        IamResourceScopeLevel.SYSTEM, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.GOVERNANCE, SYSTEM_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.CRITICAL),
                new PermissionDef(IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, "NOTIFICATION",
                        "System Notification Management",
                        "Manage system notification templates, rules, deliveries, and retries",
                        IamResourceScopeLevel.SYSTEM, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.NOTIFICATION_ADMIN, SYSTEM_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.ORGANIZATION_MANAGEMENT, "WORKSPACE",
                        "Organization Management",
                        "Manage organization records and workspace creation under organizations",
                        IamResourceScopeLevel.ORGANIZATION, IamDataAccessPolicy.ANCESTOR_INHERITED,
                        IamPermissionCategory.ORGANIZATION_ADMIN, ORGANIZATION_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.WORKSPACE_MANAGEMENT, "WORKSPACE",
                        "Workspace Management",
                        "Manage workspace records and workspace settings",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.ANCESTOR_INHERITED,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, "WORKSPACE",
                        "Workspace Access Management",
                        "Manage workspace members, invitations, join requests, and access grants",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.ACCESS_CONTROL, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.TEAM_MANAGEMENT, "WORKSPACE",
                        "Team Management",
                        "Manage workspace teams",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.TEAM_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, "WORKSPACE",
                        "Workspace Member Management",
                        "Manage workspace members",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.MEMBER_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.TEAM_MEMBER_MANAGEMENT, "WORKSPACE",
                        "Team Member Management",
                        "Manage team members",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.MEMBER_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, "WORKSPACE",
                        "Workspace Role Management",
                        "Manage workspace roles and role assignments",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.ACCESS_CONTROL, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, "KNOWLEDGE",
                        "Document Type Management",
                        "Manage workspace document types",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.CONTENT_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_MANAGEMENT, "PROJECT",
                        "Project Management",
                        "Manage projects within a workspace",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, "PROJECT",
                        "Project Phase Management",
                        "Manage project phases within a workspace",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_WBS_MANAGEMENT, "PROJECT",
                        "WBS Node Management",
                        "Manage WBS nodes within projects",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_TASK_MANAGEMENT, "PROJECT",
                        "Task Management",
                        "Manage tasks within projects",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM));
    }

    private String toTitle(String actionCode) {
        String[] words = actionCode.toLowerCase().split("_");
        StringBuilder title = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!title.isEmpty()) {
                title.append(' ');
            }
            title.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return title.toString();
    }

    private boolean needsPermissionSync(IamPermission permission, PermissionDef def) {
        return !permission.moduleCode().equals(def.moduleCode())
                || !permission.name().equals(def.name())
                || !Objects.equals(permission.description(), def.description())
                || permission.resourceScopeLevel() != def.resourceScopeLevel()
                || permission.dataAccessPolicy() != def.dataAccessPolicy()
                || permission.permissionCategory() != def.permissionCategory()
                || !permission.assignableSubjectTypes().equals(def.assignableSubjectTypes())
                || permission.riskLevel() != def.riskLevel()
                || permission.status() != IamPermissionStatus.ACTIVE;
    }

    private boolean needsActionSync(IamPermissionActionDefinition action, String name,
                                    String description, UUID rightId) {
        return !action.name().equals(name)
                || !Objects.equals(action.description(), description)
                || !Objects.equals(action.rightId(), rightId)
                || action.status() != IamPermissionStatus.ACTIVE;
    }

    private int deactivateObsoletePermissions() {
        int deactivated = 0;
        for (String code : List.of(IamPermissionCodes.ROLE_MANAGEMENT)) {
            Optional<IamPermission> existing = permissionRepository.findByCode(IamPermissionCode.of(code));
            if (existing.isPresent() && existing.get().status() == IamPermissionStatus.ACTIVE) {
                permissionRepository.save(existing.get().deactivate());
                deactivated++;
            }
        }
        return deactivated;
    }

    private record PermissionDef(
            String code,
            String moduleCode,
            String name,
            String description,
            IamResourceScopeLevel resourceScopeLevel,
            IamDataAccessPolicy dataAccessPolicy,
            IamPermissionCategory permissionCategory,
            Set<IamPermissionAssignableSubjectType> assignableSubjectTypes,
            IamPermissionRiskLevel riskLevel) {}
}
