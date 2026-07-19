package com.company.scopery.modules.aiplanning.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class AiPlanningApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects/{projectId}/ai-planning";
    public static final String RUNS = BASE + "/runs";
    public static final String SUGGESTIONS = BASE + "/suggestions";
    private AiPlanningApiPaths() {}
}
