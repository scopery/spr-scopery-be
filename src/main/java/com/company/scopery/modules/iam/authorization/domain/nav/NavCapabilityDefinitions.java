package com.company.scopery.modules.iam.authorization.domain.nav;

import com.company.scopery.modules.iam.authorization.domain.enums.NavCapabilityPack;
import com.company.scopery.modules.iam.shared.constant.IamAuthorities;
import com.company.scopery.modules.iam.shared.constant.IamPermissionAction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Nav capability key → authority for pack evaluation.
 * Derived keys (*.directory without suffix) are computed as OR of children in the query service.
 */
public final class NavCapabilityDefinitions {

    private NavCapabilityDefinitions() {}

    public record Gate(String key, IamPermissionAction authority) {}

    public static List<Gate> gatesFor(NavCapabilityPack pack) {
        return switch (pack) {
            case NAV_ORG -> ORG_GATES;
            case NAV_WORKSPACE -> WORKSPACE_GATES;
            case NAV_PROJECT -> PROJECT_GATES;
            case NAV_SETTINGS -> SETTINGS_GATES;
        };
    }

    /** Child keys whose OR becomes the parent directory entry. */
    public static Map<String, List<String>> derivedOrKeys(NavCapabilityPack pack) {
        return switch (pack) {
            case NAV_ORG -> Map.of(
                    "org.directory", List.of(
                            "org.directory.members",
                            "org.directory.invitations",
                            "org.directory.teams"));
            case NAV_WORKSPACE -> Map.of(
                    "workspace.directory", List.of(
                            "workspace.directory.members",
                            "workspace.directory.teams",
                            "workspace.directory.invitations",
                            "workspace.directory.join_requests"));
            case NAV_PROJECT -> Map.of(
                    "project.directory", List.of("project.directory.members"));
            case NAV_SETTINGS -> Map.of();
        };
    }

    private static final List<Gate> ORG_GATES = List.of(
            new Gate("org.directory.members", IamAuthorities.ORGANIZATION_MANAGE),
            new Gate("org.directory.invitations", IamAuthorities.ORGANIZATION_MANAGE),
            new Gate("org.directory.teams", IamAuthorities.TEAM_VIEW),
            new Gate("org.create_workspace", IamAuthorities.ORGANIZATION_CREATE_WORKSPACE),
            new Gate("org.settings", IamAuthorities.ORGANIZATION_MANAGE)
    );

    private static final List<Gate> WORKSPACE_GATES = List.of(
            new Gate("workspace.overview", IamAuthorities.WORKSPACE_VIEW),
            new Gate("workspace.activity", IamAuthorities.WORKSPACE_MANAGE),
            new Gate("workspace.projects", IamAuthorities.PROJECT_VIEW),
            new Gate("workspace.projects.create", IamAuthorities.PROJECT_CREATE),
            new Gate("workspace.capacity", IamAuthorities.CAPACITY_VIEW),
            new Gate("workspace.clients", IamAuthorities.EXTERNAL_PARTY_VIEW),
            new Gate("workspace.applications", IamAuthorities.REQUIREMENT_VIEW),
            new Gate("workspace.support", IamAuthorities.WORKSPACE_VIEW),
            new Gate("workspace.forms", IamAuthorities.WORKSPACE_VIEW),
            new Gate("workspace.directory.members", IamAuthorities.WORKSPACE_MEMBER_VIEW),
            new Gate("workspace.directory.teams", IamAuthorities.TEAM_VIEW),
            new Gate("workspace.directory.invitations", IamAuthorities.WORKSPACE_INVITE_MEMBER),
            new Gate("workspace.directory.join_requests", IamAuthorities.WORKSPACE_MANAGE_JOIN_REQUEST),
            new Gate("workspace.settings", IamAuthorities.WORKSPACE_MANAGE_SETTING),
            new Gate("common.document_hub", IamAuthorities.DOCUMENT_HUB_VIEW),
            new Gate("common.search", IamAuthorities.GLOBAL_SEARCH_USE),
            new Gate("common.my_insights", IamAuthorities.WORK_INBOX_VIEW),
            new Gate("common.work_inbox", IamAuthorities.WORK_INBOX_VIEW),
            new Gate("common.notifications", IamAuthorities.NAVIGATION_VIEW),
            new Gate("common.ai_assistant", IamAuthorities.NAVIGATION_VIEW)
    );

    private static final List<Gate> PROJECT_GATES = List.of(
            new Gate("project.overview", IamAuthorities.PROJECT_VIEW),
            new Gate("project.work", IamAuthorities.PROJECT_TASK_VIEW),
            new Gate("project.wbs", IamAuthorities.PROJECT_WBS_VIEW),
            new Gate("project.timeline", IamAuthorities.PROJECT_WBS_VIEW),
            new Gate("project.schedule", IamAuthorities.PROJECT_PHASE_VIEW),
            new Gate("project.resources", IamAuthorities.PROJECT_ALLOCATION_VIEW),
            new Gate("project.scope", IamAuthorities.SCOPE_VIEW),
            new Gate("project.deliverables", IamAuthorities.DELIVERABLE_VIEW),
            new Gate("project.requirements", IamAuthorities.REQUIREMENT_VIEW),
            new Gate("project.functional_catalog", IamAuthorities.REQUIREMENT_VIEW),
            new Gate("project.application_structure", IamAuthorities.REQUIREMENT_VIEW),
            new Gate("project.traceability", IamAuthorities.REQUIREMENT_VIEW),
            new Gate("project.quality", IamAuthorities.QUALITY_VIEW),
            new Gate("project.test_plans", IamAuthorities.TEST_VIEW),
            new Gate("project.defects", IamAuthorities.DEFECT_VIEW),
            new Gate("project.releases", IamAuthorities.RELEASE_VIEW),
            new Gate("project.estimation", IamAuthorities.ESTIMATION_VIEW),
            new Gate("project.financials", IamAuthorities.PROJECT_FINANCE_VIEW),
            new Gate("project.profitability", IamAuthorities.PROFITABILITY_SUMMARY_VIEW),
            new Gate("project.quotes", IamAuthorities.QUOTE_VIEW),
            new Gate("project.raid", IamAuthorities.RAID_VIEW),
            new Gate("project.decisions", IamAuthorities.DECISION_VIEW),
            new Gate("project.baselines", IamAuthorities.PROJECT_BASELINE_VIEW),
            new Gate("project.change_requests", IamAuthorities.CHANGE_REQUEST_VIEW),
            new Gate("project.governance", IamAuthorities.OBJECT_OWNERSHIP_VIEW),
            new Gate("project.meetings", IamAuthorities.PROJECT_MEETING_VIEW),
            new Gate("project.client_collaboration", IamAuthorities.CLIENT_PORTAL_VIEW),
            new Gate("project.ai_planning", IamAuthorities.AI_PROJECT_PLANNING_VIEW),
            new Gate("project.recommendations", IamAuthorities.AI_PROJECT_PLANNING_VIEW),
            new Gate("project.reports", IamAuthorities.REPORTING_REPORT_VIEW),
            new Gate("project.dashboard", IamAuthorities.REPORTING_DASHBOARD_VIEW),
            new Gate("project.directory.members", IamAuthorities.WORKSPACE_MEMBER_VIEW)
    );

    private static final List<Gate> SETTINGS_GATES = List.of(
            new Gate("settings.workspace_entry", IamAuthorities.WORKSPACE_MANAGE_SETTING),
            new Gate("settings.project_entry", IamAuthorities.PROJECT_UPDATE)
    );

    /** Always-true personal settings key (no IAM gate). */
    public static final String SETTINGS_PERSONAL = "settings.personal";

    public static Map<String, Boolean> emptyFalseMap(NavCapabilityPack pack) {
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (Gate gate : gatesFor(pack)) {
            map.put(gate.key(), false);
        }
        for (String derived : derivedOrKeys(pack).keySet()) {
            map.put(derived, false);
        }
        if (pack == NavCapabilityPack.NAV_SETTINGS) {
            map.put(SETTINGS_PERSONAL, true);
        }
        return map;
    }
}
