package com.company.scopery.modules.profitability.shared.constant;

public final class ProfitabilityApiPaths {
    private ProfitabilityApiPaths() {}

    public static final String PROFILE = "/api/projects/{projectId}/profitability/profile";
    public static final String SUMMARY = "/api/projects/{projectId}/profitability/summary";
    public static final String SUMMARY_PORTAL = "/api/projects/{projectId}/profitability/summary/portal";
    public static final String SUMMARY_REBUILD = "/api/projects/{projectId}/profitability/summary/rebuild";
    public static final String THRESHOLD = "/api/projects/{projectId}/profitability/thresholds";
    public static final String COST_SOURCES = "/api/projects/{projectId}/profitability/cost-sources";
    public static final String REVENUE_SOURCES = "/api/projects/{projectId}/profitability/revenue-sources";
    public static final String COST_FORECASTS = "/api/projects/{projectId}/profitability/cost-forecasts";
    public static final String REVENUE_FORECASTS = "/api/projects/{projectId}/profitability/revenue-forecasts";
    public static final String PLANS = "/api/projects/{projectId}/profitability/plans";
    public static final String ADJUSTMENTS = "/api/projects/{projectId}/profitability/adjustments";
    public static final String VARIANCE = "/api/projects/{projectId}/profitability/variance";
    public static final String RISK_FLAGS = "/api/projects/{projectId}/profitability/risk-flags";
    public static final String THRESHOLD_POLICY = "/api/projects/{projectId}/profitability/threshold-policy";
    public static final String WORKSPACE_RATE_CARDS = "/api/workspaces/{workspaceId}/profitability/rate-cards";
    public static final String PROJECT_RATE_CARDS = "/api/projects/{projectId}/profitability/rate-cards";
}
