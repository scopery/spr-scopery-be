package com.company.scopery.modules.projectfinance.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class ProjectFinanceApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String FINANCE_SCENARIOS = BASE + "/{projectId}/finance-scenarios";
    public static final String CURRENT_FINANCE = BASE + "/{projectId}/finance/current";

    private ProjectFinanceApiPaths() {}
}
