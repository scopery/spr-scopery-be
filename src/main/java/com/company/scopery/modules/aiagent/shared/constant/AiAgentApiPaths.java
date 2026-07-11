package com.company.scopery.modules.aiagent.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class AiAgentApiPaths {

    private static final String BASE = ApiPaths.BASE_PATH + "/ai-agent";

    public static final String PROVIDERS                    = BASE + "/providers";
    public static final String MODELS                       = BASE + "/models";
    public static final String MODEL_DEPLOYMENTS            = BASE + "/model-deployments";
    public static final String MODEL_PARAMETER_CAPABILITIES = BASE + "/model-parameter-capabilities";
    public static final String AGENTS                       = BASE + "/agents";
    public static final String PROMPT_TEMPLATES             = BASE + "/prompt-templates";
    public static final String PROMPT_VERSIONS              = BASE + "/prompt-versions";
    public static final String EVENT_CONFIGS                = BASE + "/event-configs";
    public static final String USAGE_POLICIES               = BASE + "/usage-policies";
    public static final String EXECUTION_LOGS               = BASE + "/execution-logs";
    public static final String EXECUTIONS                   = BASE + "/executions";
    public static final String PROVIDER_SECRETS             = BASE + "/provider-secrets";
    public static final String PLAYGROUND                   = BASE + "/playground";

    private AiAgentApiPaths() {}
}
