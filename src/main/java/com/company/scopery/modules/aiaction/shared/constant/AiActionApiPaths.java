package com.company.scopery.modules.aiaction.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class AiActionApiPaths {

    public static final String BASE       = ApiPaths.BASE_PATH + "/ai-actions";
    public static final String TOOLS      = BASE + "/tools";
    public static final String REQUESTS   = BASE + "/requests";
    public static final String PLANS      = BASE + "/plans";
    public static final String EXECUTIONS = BASE + "/executions";
    public static final String HISTORY    = BASE + "/history";
    public static final String WS         = "/api/ws/ai-actions";

    private AiActionApiPaths() {}
}
