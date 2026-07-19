package com.company.scopery.modules.projectbaseline.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProjectBaselineApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String BASELINES = BASE + "/{projectId}/baselines";
    public static final String CURRENT_BASELINE = BASE + "/{projectId}/baseline/current";
    public static final String CHANGE_REQUESTS = BASE + "/{projectId}/change-requests";
    public static final String CHANGE_REQUEST_ITEMS = CHANGE_REQUESTS + "/{changeRequestId}/items";
    public static final String CHANGE_REQUEST_IMPACT = CHANGE_REQUESTS + "/{changeRequestId}/impact";
    public static final String CHANGE_ORDERS = CHANGE_REQUESTS + "/{changeRequestId}/change-orders";
    public static final String CHANGE_ORDER_BY_ID = BASE + "/{projectId}/change-orders/{changeOrderId}";

    private ProjectBaselineApiPaths() {}
}
