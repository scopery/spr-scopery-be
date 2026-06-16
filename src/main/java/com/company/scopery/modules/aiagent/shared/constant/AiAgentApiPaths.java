package com.company.scopery.modules.aiagent.shared.constant;

import com.company.scopery.common.constant.ApiPaths;

public final class AiAgentApiPaths {

    public static final String API_V1_AI_AGENT              = ApiPaths.V1 + "/ai-agent";
    public static final String PROVIDERS                    = API_V1_AI_AGENT + "/providers";
    public static final String MODELS                       = API_V1_AI_AGENT + "/models";
    public static final String MODEL_DEPLOYMENTS            = API_V1_AI_AGENT + "/model-deployments";
    public static final String MODEL_PARAMETER_CAPABILITIES = API_V1_AI_AGENT + "/model-parameter-capabilities";
    public static final String AGENTS                       = API_V1_AI_AGENT + "/agents";
    public static final String PROMPT_TEMPLATES             = API_V1_AI_AGENT + "/prompt-templates";
    public static final String PROMPT_VERSIONS              = API_V1_AI_AGENT + "/prompt-versions";
    public static final String EVENT_CONFIGS                = API_V1_AI_AGENT + "/event-configs";
    public static final String USAGE_POLICIES               = API_V1_AI_AGENT + "/usage-policies";
    public static final String EXECUTION_LOGS               = API_V1_AI_AGENT + "/execution-logs";
    public static final String EXECUTIONS                   = API_V1_AI_AGENT + "/executions";
    public static final String PROVIDER_SECRETS             = API_V1_AI_AGENT + "/provider-secrets";
    public static final String PLAYGROUND                   = API_V1_AI_AGENT + "/playground";

    private AiAgentApiPaths() {}
}
