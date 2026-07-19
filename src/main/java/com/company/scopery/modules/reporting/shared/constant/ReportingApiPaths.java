package com.company.scopery.modules.reporting.shared.constant;
import com.company.scopery.common.constant.ApiPaths;
public final class ReportingApiPaths {
    public static final String DASHBOARD = ApiPaths.BASE_PATH + "/projects/{projectId}/dashboard";
    public static final String PROJECT_REPORTS = ApiPaths.BASE_PATH + "/projects/{projectId}/reports";
    public static final String DEFINITIONS = ApiPaths.BASE_PATH + "/reports/definitions";
    public static final String RUNS = ApiPaths.BASE_PATH + "/reports/runs";
    public static final String EXPORTS = ApiPaths.BASE_PATH + "/reports/exports";
    public static final String ACTIVITY_FEED = ApiPaths.BASE_PATH + "/projects/{projectId}/activity-feed";
    private ReportingApiPaths() {}
}
