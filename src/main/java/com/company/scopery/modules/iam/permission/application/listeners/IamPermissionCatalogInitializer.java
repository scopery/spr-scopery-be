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
import com.company.scopery.modules.iam.shared.constant.IamActionCodes;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;
import com.company.scopery.modules.iam.shared.constant.IamPermissionCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbc;

    public IamPermissionCatalogInitializer(IamPermissionRepository permissionRepository,
                                           IamPermissionActionDefinitionRepository actionRepository,
                                           JdbcTemplate jdbc) {
        this.permissionRepository = permissionRepository;
        this.actionRepository = actionRepository;
        this.jdbc = jdbc;
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
        List<UUID> ids = jdbc.query(
                "SELECT id FROM iam_right WHERE code = ?",
                (rs, n) -> rs.getObject("id", UUID.class),
                rightCode);
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        log.warn("IAM permission catalog: backing right not found: {}", rightCode);
        return null;
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
                new PermissionDef(IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, "KNOWLEDGE",
                        "Document Type Field Management",
                        "Manage metadata fields on document types",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.CONTENT_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.KNOWLEDGE_CLASSIFICATION_MANAGEMENT, "KNOWLEDGE",
                        "Knowledge Classification Management",
                        "View document classification vocabulary",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.CONTENT_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.LOW),
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
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, "PROJECT",
                        "Phase Definition Management",
                        "Manage workspace phase definition catalog entries",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, "PROJECT",
                        "Project Template Management",
                        "Manage project templates, versions, and apply workflows",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, "CAPACITY",
                        "Capacity Calendar Management",
                        "Manage workspace working calendars, day rules, and calendar exceptions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, "CAPACITY",
                        "Capacity Profile Management",
                        "Manage user capacity profiles within a workspace",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, "CAPACITY",
                        "Project Allocation Management",
                        "Manage project resource allocations within a workspace",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.CAPACITY_MANAGEMENT, "CAPACITY",
                        "Capacity Management",
                        "View and calculate workspace-wide resource capacity",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.LOW),
                new PermissionDef(IamPermissionCodes.COST_ROLE_MANAGEMENT, "RATE_CARD",
                        "Cost Role Management",
                        "Manage planning cost roles used for rate estimation",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.RATE_CARD_MANAGEMENT, "RATE_CARD",
                        "Rate Card Management",
                        "Manage rate cards and publish versions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.RATE_CARD_LINE_MANAGEMENT, "RATE_CARD",
                        "Rate Card Line Management",
                        "Manage rate card lines (CCH / billing rates)",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, "RATE_CARD",
                        "Inflation Policy Management",
                        "Manage inflation / escalation policies",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.RATE_RESOLUTION_MANAGEMENT, "RATE_CARD",
                        "Rate Resolution Management",
                        "Resolve rates and preview task labor cost",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.LOW),
                new PermissionDef(IamPermissionCodes.MEMBER_COST_ROLE_MANAGEMENT, "RATE_CARD",
                        "Member Cost Role Management",
                        "Assign default cost roles to workspace members",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.ESTIMATION_MANAGEMENT, "ESTIMATION",
                        "Estimation Management",
                        "Create estimation runs and view labor estimate roll-ups",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, "PROJECT_FINANCE",
                        "Project Finance Management",
                        "Create planned finance scenarios, costs, revenue split, and margin summaries",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.QUOTE_MANAGEMENT, "QUOTE",
                        "Quote Management",
                        "Create commercial quotes, versions, lines, terms, and lifecycle actions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, "PROJECT_BASELINE",
                        "Project Baseline Management",
                        "Create, validate, approve, and mark current project baselines",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, "CHANGE_REQUEST",
                        "Change Request Management",
                        "Create, submit, approve, and apply controlled project change requests",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, "CHANGE_ORDER",
                        "Change Order Management",
                        "Create commercial change order records from approved change requests",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, "PROJECT_NOTIFICATION",
                        "Project Notification Management",
                        "Manage project/task notification subscriptions, preferences, and reminders",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, "AI_PLANNING",
                        "AI Project Planning Management",
                        "Run, review, accept, reject, and safely apply AI planning suggestions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.REPORTING_MANAGEMENT, "REPORTING",
                        "Reporting Management",
                        "View dashboards, run reports, and export access-controlled analytics",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.SCOPE_MANAGEMENT, "SCOPE",
                        "Scope Management",
                        "Manage project scope packages and scope items",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.DELIVERABLE_MANAGEMENT, "DELIVERABLE",
                        "Deliverable Management",
                        "Manage deliverables, acceptance criteria, evidence, and formal acceptance",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.RAID_MANAGEMENT, "RAID",
                        "RAID Management",
                        "Manage project risks, assumptions, issues, dependencies, and actions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.DECISION_MANAGEMENT, "DECISION",
                        "Decision Management",
                        "Record and decide project governance decisions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.QUALITY_MANAGEMENT, "QUALITY",
                        "Quality Management",
                        "Manage project quality plans",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.TEST_MANAGEMENT, "TEST",
                        "Test Management",
                        "Manage test plans, cases, and runs",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.DEFECT_MANAGEMENT, "DEFECT",
                        "Defect Management",
                        "Manage defects and defect lifecycle",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.RELEASE_MANAGEMENT, "RELEASE",
                        "Release Management",
                        "Manage release packages and readiness",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.DEPLOYMENT_MANAGEMENT, "DEPLOYMENT",
                        "Deployment Management",
                        "Record deployment environments and deployment history",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, "DOCUMENT_HUB",
                        "Document Hub Management",
                        "Manage project documents, versions, templates, and generation",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.REQUIREMENT_MANAGEMENT, "REQUIREMENT",
                        "Requirement Management",
                        "Manage requirements and traceability links",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.EXTERNAL_PARTY_MANAGEMENT, "EXTERNAL_PARTY",
                        "External Party Management",
                        "Manage external organizations, contacts, and stakeholders",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.CLIENT_PORTAL_MANAGEMENT, "CLIENT_PORTAL",
                        "Client Portal Management",
                        "Manage client portal access grants and review requests",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.COLLABORATION_MANAGEMENT, "COLLABORATION",
                        "Collaboration Management",
                        "Manage meetings, minutes, agenda, participants, and meeting actions",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.COMMENT_MANAGEMENT, "COMMENT",
                        "Comment Management",
                        "Manage comment threads and comments on project artifacts",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.LOW),
                new PermissionDef(IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, "PRODUCTIVITY",
                        "Productivity Management",
                        "Search, saved views, favorites, and work inbox",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.LOW),
                new PermissionDef(IamPermissionCodes.CONFIGURATION_MANAGEMENT, "CONFIGURATION",
                        "Configuration Management",
                        "Custom fields, forms, tags, and taxonomies",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, "GOVERNANCE",
                        "Object Governance Management",
                        "Ownership, versioning, locking, and restore governance",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH),
                new PermissionDef(IamPermissionCodes.ADVANCED_NOTIFICATION_MANAGEMENT, "NOTIFICATION",
                        "Advanced Notification Management",
                        "Preferences, digests, reminders, and alert rules",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.MEDIUM),
                new PermissionDef(IamPermissionCodes.PROFITABILITY_MANAGEMENT, "PROFITABILITY",
                        "Profitability Management",
                        "Project revenue, cost, margin, and profitability visibility",
                        IamResourceScopeLevel.WORKSPACE, IamDataAccessPolicy.SCOPE_WIDE,
                        IamPermissionCategory.WORKSPACE_ADMIN, WORKSPACE_ASSIGNABLE_SUBJECTS,
                        IamPermissionRiskLevel.HIGH));
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
