package com.company.scopery.modules.resourcecapacity.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class CapacityApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/capacity";
    private static final String WS = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/resources";
    private static final String PR = ApiPaths.BASE_PATH + "/projects/{projectId}/resources";

    public static final String CALENDARS = BASE + "/calendars";
    public static final String CALENDAR_DAY_RULES = BASE + "/calendars/{calendarId}/day-rules";
    public static final String CALENDAR_EXCEPTIONS = BASE + "/calendars/{calendarId}/exceptions";
    public static final String USER_PROFILES = BASE + "/user-profiles";
    public static final String PROJECT_ALLOCATIONS = BASE + "/project-allocations";
    public static final String USER_AVAILABILITY = BASE + "/users/{userId}/availability";
    public static final String WORKSPACE_OVERVIEW = BASE + "/workspaces/{workspaceId}/overview";
    public static final String PROJECT_ALLOCATION_SUMMARY = BASE + "/projects/{projectId}/allocations/summary";
    public static final String OVER_ALLOCATIONS = BASE + "/over-allocations";
    public static final String CALCULATE = BASE + "/calculate";

    // Phase 37
    public static final String RESOURCES = WS;
    public static final String RESOURCE_BY_ID = WS + "/{resourceId}";
    public static final String RESOURCE_ARCHIVE = WS + "/{resourceId}/archive";
    public static final String RESOURCE_SYNC = WS + "/sync-from-members";
    public static final String ROLES = WS + "/roles";
    public static final String ROLE_BY_ID = WS + "/roles/{roleId}";
    public static final String ROLE_ARCHIVE = WS + "/roles/{roleId}/archive";
    public static final String SKILLS = WS + "/skills";
    public static final String SKILL_BY_ID = WS + "/skills/{skillId}";
    public static final String SKILL_ARCHIVE = WS + "/skills/{skillId}/archive";
    public static final String RESOURCE_SKILLS = WS + "/{resourceId}/skills";
    public static final String RESOURCE_SKILL_BY_ID = WS + "/{resourceId}/skills/{skillId}";
    public static final String TASK_ASSIGNMENTS = ApiPaths.BASE_PATH + "/projects/{projectId}/tasks/{taskId}/resource-assignments";
    public static final String EFFORT_ESTIMATES = PR + "/effort-estimates";
    public static final String EFFORT_FORECASTS = PR + "/effort-forecasts";
    public static final String EFFORT_FORECAST_REBUILD = PR + "/effort-forecasts/rebuild";
    public static final String ACTUAL_EFFORT = PR + "/actual-effort-records";
    public static final String UTILIZATION = WS + "/{resourceId}/utilization";
    public static final String UTILIZATION_REBUILD = WS + "/{resourceId}/utilization/rebuild";
    public static final String CAPACITY_SUMMARY = PR + "/capacity-summary";
    public static final String CAPACITY_SUMMARY_REBUILD = PR + "/capacity-summary/rebuild";
    public static final String WORKLOAD_SNAPSHOTS = PR + "/workload-snapshots";
    public static final String RISK_FLAGS = PR + "/risk-flags";
    public static final String CONFLICTS = PR + "/assignment-conflicts";
    public static final String CONFLICTS_RECALCULATE = PR + "/conflicts/recalculate";
    public static final String COST_INPUTS = PR + "/cost-inputs";
    public static final String COST_INPUTS_REBUILD = PR + "/cost-inputs/rebuild";
    public static final String THRESHOLD_WS = WS + "/utilization-threshold-policy";
    public static final String THRESHOLD_PR = PR + "/utilization-threshold-policy";

    private CapacityApiPaths() {}
}
