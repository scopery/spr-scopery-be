package com.company.scopery.modules.aiagent.shared.constant;

public final class AiAgentActivityActions {

    // Provider
    public static final String CREATE_PROVIDER     = "CREATE_PROVIDER";
    public static final String UPDATE_PROVIDER     = "UPDATE_PROVIDER";
    public static final String ACTIVATE_PROVIDER   = "ACTIVATE_PROVIDER";
    public static final String DEACTIVATE_PROVIDER = "DEACTIVATE_PROVIDER";

    // AI Model
    public static final String CREATE_AI_MODEL     = "CREATE_AI_MODEL";
    public static final String UPDATE_AI_MODEL     = "UPDATE_AI_MODEL";
    public static final String ACTIVATE_AI_MODEL   = "ACTIVATE_AI_MODEL";
    public static final String DEACTIVATE_AI_MODEL = "DEACTIVATE_AI_MODEL";

    // Model Deployment
    public static final String CREATE_MODEL_DEPLOYMENT       = "CREATE_MODEL_DEPLOYMENT";
    public static final String UPDATE_MODEL_DEPLOYMENT       = "UPDATE_MODEL_DEPLOYMENT";
    public static final String ACTIVATE_MODEL_DEPLOYMENT     = "ACTIVATE_MODEL_DEPLOYMENT";
    public static final String DEACTIVATE_MODEL_DEPLOYMENT   = "DEACTIVATE_MODEL_DEPLOYMENT";
    public static final String SET_DEFAULT_MODEL_DEPLOYMENT  = "SET_DEFAULT_MODEL_DEPLOYMENT";

    // Model Parameter Capability
    public static final String CREATE_MODEL_PARAMETER_CAPABILITY     = "CREATE_MODEL_PARAMETER_CAPABILITY";
    public static final String UPDATE_MODEL_PARAMETER_CAPABILITY     = "UPDATE_MODEL_PARAMETER_CAPABILITY";
    public static final String ACTIVATE_MODEL_PARAMETER_CAPABILITY   = "ACTIVATE_MODEL_PARAMETER_CAPABILITY";
    public static final String DEACTIVATE_MODEL_PARAMETER_CAPABILITY = "DEACTIVATE_MODEL_PARAMETER_CAPABILITY";

    // Agent
    public static final String CREATE_AGENT     = "CREATE_AGENT";
    public static final String UPDATE_AGENT     = "UPDATE_AGENT";
    public static final String ACTIVATE_AGENT   = "ACTIVATE_AGENT";
    public static final String DEACTIVATE_AGENT = "DEACTIVATE_AGENT";

    // Prompt Template
    public static final String CREATE_PROMPT_TEMPLATE     = "CREATE_PROMPT_TEMPLATE";
    public static final String UPDATE_PROMPT_TEMPLATE     = "UPDATE_PROMPT_TEMPLATE";
    public static final String ACTIVATE_PROMPT_TEMPLATE   = "ACTIVATE_PROMPT_TEMPLATE";
    public static final String DEACTIVATE_PROMPT_TEMPLATE = "DEACTIVATE_PROMPT_TEMPLATE";

    // Prompt Version
    public static final String CREATE_PROMPT_VERSION   = "CREATE_PROMPT_VERSION";
    public static final String UPDATE_PROMPT_VERSION   = "UPDATE_PROMPT_VERSION";
    public static final String ACTIVATE_PROMPT_VERSION = "ACTIVATE_PROMPT_VERSION";
    public static final String ARCHIVE_PROMPT_VERSION  = "ARCHIVE_PROMPT_VERSION";

    // Event Config
    public static final String CREATE_EVENT_CONFIG     = "CREATE_EVENT_CONFIG";
    public static final String UPDATE_EVENT_CONFIG     = "UPDATE_EVENT_CONFIG";
    public static final String ACTIVATE_EVENT_CONFIG   = "ACTIVATE_EVENT_CONFIG";
    public static final String DEACTIVATE_EVENT_CONFIG = "DEACTIVATE_EVENT_CONFIG";

    // Usage Policy
    public static final String CREATE_USAGE_POLICY     = "CREATE_USAGE_POLICY";
    public static final String UPDATE_USAGE_POLICY     = "UPDATE_USAGE_POLICY";
    public static final String ACTIVATE_USAGE_POLICY   = "ACTIVATE_USAGE_POLICY";
    public static final String DEACTIVATE_USAGE_POLICY = "DEACTIVATE_USAGE_POLICY";

    // Execution Log
    public static final String CREATE_EXECUTION_LOG      = "CREATE_EXECUTION_LOG";
    public static final String MARK_EXECUTION_RUNNING    = "MARK_EXECUTION_RUNNING";
    public static final String MARK_EXECUTION_SUCCEEDED  = "MARK_EXECUTION_SUCCEEDED";
    public static final String MARK_EXECUTION_FAILED     = "MARK_EXECUTION_FAILED";
    public static final String CANCEL_EXECUTION          = "CANCEL_EXECUTION";

    // Execution (AI Provider)
    public static final String EXECUTE_EVENT             = "EXECUTE_EVENT";
    public static final String EXECUTE_EVENT_CONFIG      = "EXECUTE_EVENT_CONFIG";

    // Provider Secret
    public static final String SET_PROVIDER_SECRET        = "SET_PROVIDER_SECRET";
    public static final String ROTATE_PROVIDER_SECRET     = "ROTATE_PROVIDER_SECRET";
    public static final String DEACTIVATE_PROVIDER_SECRET = "DEACTIVATE_PROVIDER_SECRET";

    private AiAgentActivityActions() {}
}
