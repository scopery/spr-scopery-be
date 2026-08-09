package com.company.scopery.modules.finance.shared.constant;

public final class FinanceApiPaths {
    private FinanceApiPaths() {}

    public static final String SCENARIOS = "/api/projects/{projectId}/finance-scenarios";
    public static final String SCENARIO = "/api/projects/{projectId}/finance-scenarios/{scenarioId}";
    public static final String CURRENT = "/api/projects/{projectId}/finance/current";
    public static final String FINANCE_BASE = "/api/projects/{projectId}/finance";
}
