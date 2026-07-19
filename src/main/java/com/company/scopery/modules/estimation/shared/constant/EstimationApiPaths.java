package com.company.scopery.modules.estimation.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class EstimationApiPaths {
    private static final String BASE = ApiPaths.BASE_PATH + "/projects";

    public static final String ESTIMATION_RUNS = BASE + "/{projectId}/estimation-runs";
    public static final String CURRENT_ESTIMATION = BASE + "/{projectId}/estimation/current";
    public static final String PREVIEW_RATE_IMPACT = BASE + "/{projectId}/estimation/preview-rate-impact";

    private EstimationApiPaths() {}
}
