package com.company.scopery.modules.profitability.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProfitabilityApiPaths {
    private static final String BASE    = ApiPaths.BASE_PATH + "/projects/{projectId}/profitability";
    private static final String WS_BASE = ApiPaths.BASE_PATH + "/workspaces/{workspaceId}/profitability";

    // Profile
    public static final String PROFILE        = BASE + "/profile";

    // Revenue
    public static final String REVENUE_SOURCES         = BASE + "/revenue-sources";
    public static final String REVENUE_FORECASTS       = BASE + "/revenue-forecasts";
    public static final String REVENUE_FORECAST_REBUILD = BASE + "/revenue-forecasts/rebuild-current";

    // Cost
    public static final String COST_SOURCES         = BASE + "/cost-sources";
    public static final String COST_FORECASTS       = BASE + "/cost-forecasts";
    public static final String COST_FORECAST_REBUILD = BASE + "/cost-forecasts/rebuild-current";

    // Rate card
    public static final String WORKSPACE_RATE_CARDS = WS_BASE + "/rate-cards";
    public static final String PROJECT_RATE_CARDS   = BASE + "/rate-cards";

    // Plan / version
    public static final String PLANS        = BASE + "/plans";
    public static final String PLAN_VERSIONS = BASE + "/plan-versions";

    // Adjustment
    public static final String ADJUSTMENTS = BASE + "/adjustments";

    // Snapshot / summary
    public static final String SNAPSHOTS      = BASE + "/snapshots";
    public static final String SUMMARY        = BASE + "/summary";
    public static final String SUMMARY_REBUILD = BASE + "/summary/rebuild";
    public static final String SUMMARY_PORTAL = BASE + "/summary/portal";

    // Workspace-level summary
    public static final String WS_SUMMARY    = WS_BASE + "/summary";
    public static final String WS_BY_CLIENT  = WS_BASE + "/by-client";
    public static final String WS_BY_PROJECT = WS_BASE + "/by-project";

    // Risk / variance
    public static final String RISK_FLAGS = BASE + "/risk-flags";
    public static final String VARIANCE   = BASE + "/variance";

    // Threshold
    public static final String THRESHOLD    = BASE + "/threshold-policy";
    public static final String WS_THRESHOLD = WS_BASE + "/threshold-policy";

    private ProfitabilityApiPaths() {}
}
