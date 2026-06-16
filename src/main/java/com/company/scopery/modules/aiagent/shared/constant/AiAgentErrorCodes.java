package com.company.scopery.modules.aiagent.shared.constant;

/**
 * @deprecated Use {@link com.company.scopery.modules.aiagent.shared.error.AiAgentErrorCatalog} instead.
 * New code must not add values here.
 */
@Deprecated
public final class AiAgentErrorCodes {

    // Provider
    public static final String PROVIDER_NOT_FOUND                        = "PROVIDER_NOT_FOUND";
    public static final String PROVIDER_CODE_ALREADY_EXISTS              = "PROVIDER_CODE_ALREADY_EXISTS";
    public static final String DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED   = "DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED";
    public static final String ACTIVE_PROVIDER_REQUIRES_API_BASE_URL     = "ACTIVE_PROVIDER_REQUIRES_API_BASE_URL";

    // AI Model
    public static final String AI_MODEL_NOT_FOUND                        = "AI_MODEL_NOT_FOUND";
    public static final String AI_MODEL_CODE_ALREADY_EXISTS              = "AI_MODEL_CODE_ALREADY_EXISTS";
    public static final String AI_MODEL_PROVIDER_NOT_FOUND               = "AI_MODEL_PROVIDER_NOT_FOUND";
    public static final String AI_MODEL_PROVIDER_NOT_ACTIVE              = "AI_MODEL_PROVIDER_NOT_ACTIVE";
    public static final String DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED   = "DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED";

    // Model Deployment
    public static final String MODEL_DEPLOYMENT_NOT_FOUND                            = "MODEL_DEPLOYMENT_NOT_FOUND";
    public static final String MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS                  = "MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS";
    public static final String MODEL_DEPLOYMENT_MODEL_NOT_FOUND                      = "MODEL_DEPLOYMENT_MODEL_NOT_FOUND";
    public static final String MODEL_DEPLOYMENT_MODEL_NOT_ACTIVE                     = "MODEL_DEPLOYMENT_MODEL_NOT_ACTIVE";
    public static final String MODEL_DEPLOYMENT_NOT_ACTIVE                           = "MODEL_DEPLOYMENT_NOT_ACTIVE";
    public static final String DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED       = "DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED";
    public static final String MODEL_DEPLOYMENT_DEFAULT_CONFLICT                     = "MODEL_DEPLOYMENT_DEFAULT_CONFLICT";
    public static final String INVALID_MODEL_DEPLOYMENT_ENVIRONMENT                  = "INVALID_MODEL_DEPLOYMENT_ENVIRONMENT";
    public static final String INVALID_MODEL_DEPLOYMENT_STATUS                       = "INVALID_MODEL_DEPLOYMENT_STATUS";

    // Model Parameter Capability
    public static final String MODEL_PARAMETER_CAPABILITY_NOT_FOUND        = "MODEL_PARAMETER_CAPABILITY_NOT_FOUND";
    public static final String MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS   = "MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS";
    public static final String MODEL_PARAMETER_CAPABILITY_MODEL_NOT_FOUND  = "MODEL_PARAMETER_CAPABILITY_MODEL_NOT_FOUND";
    public static final String MODEL_PARAMETER_CAPABILITY_MODEL_DEPRECATED = "MODEL_PARAMETER_CAPABILITY_MODEL_DEPRECATED";
    public static final String INVALID_MODEL_PARAMETER_SUPPORT_STATUS      = "INVALID_MODEL_PARAMETER_SUPPORT_STATUS";
    public static final String INVALID_MODEL_PARAMETER_VALUE_TYPE          = "INVALID_MODEL_PARAMETER_VALUE_TYPE";
    public static final String INVALID_MODEL_PARAMETER_RANGE               = "INVALID_MODEL_PARAMETER_RANGE";
    public static final String INVALID_MODEL_PARAMETER_NULLABLE_RULE       = "INVALID_MODEL_PARAMETER_NULLABLE_RULE";
    public static final String INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR    = "INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR";

    // Agent
    public static final String AGENT_NOT_FOUND                          = "AGENT_NOT_FOUND";
    public static final String AGENT_CODE_ALREADY_EXISTS                = "AGENT_CODE_ALREADY_EXISTS";
    public static final String AGENT_DEFAULT_DEPLOYMENT_NOT_FOUND       = "AGENT_DEFAULT_DEPLOYMENT_NOT_FOUND";
    public static final String AGENT_DEFAULT_DEPLOYMENT_NOT_ACTIVE      = "AGENT_DEFAULT_DEPLOYMENT_NOT_ACTIVE";
    public static final String DEPRECATED_AGENT_CANNOT_BE_ACTIVATED     = "DEPRECATED_AGENT_CANNOT_BE_ACTIVATED";
    public static final String INVALID_AGENT_TYPE                       = "INVALID_AGENT_TYPE";
    public static final String INVALID_AGENT_STATUS                     = "INVALID_AGENT_STATUS";
    public static final String INVALID_AGENT_OUTPUT_FORMAT              = "INVALID_AGENT_OUTPUT_FORMAT";

    // Prompt Template
    public static final String PROMPT_TEMPLATE_NOT_FOUND                        = "PROMPT_TEMPLATE_NOT_FOUND";
    public static final String PROMPT_TEMPLATE_CODE_ALREADY_EXISTS              = "PROMPT_TEMPLATE_CODE_ALREADY_EXISTS";
    public static final String PROMPT_TEMPLATE_AGENT_NOT_FOUND                  = "PROMPT_TEMPLATE_AGENT_NOT_FOUND";
    public static final String PROMPT_TEMPLATE_AGENT_NOT_ACTIVE                 = "PROMPT_TEMPLATE_AGENT_NOT_ACTIVE";
    public static final String PROMPT_TEMPLATE_AGENT_DEPRECATED                 = "PROMPT_TEMPLATE_AGENT_DEPRECATED";
    public static final String DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED   = "DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED";
    public static final String INVALID_PROMPT_TEMPLATE_STATUS                   = "INVALID_PROMPT_TEMPLATE_STATUS";

    // Prompt Version
    public static final String PROMPT_VERSION_NOT_FOUND                         = "PROMPT_VERSION_NOT_FOUND";
    public static final String PROMPT_VERSION_TEMPLATE_NOT_FOUND                = "PROMPT_VERSION_TEMPLATE_NOT_FOUND";
    public static final String PROMPT_VERSION_TEMPLATE_NOT_ACTIVE               = "PROMPT_VERSION_TEMPLATE_NOT_ACTIVE";
    public static final String PROMPT_VERSION_TEMPLATE_DEPRECATED               = "PROMPT_VERSION_TEMPLATE_DEPRECATED";
    public static final String PROMPT_VERSION_CONTENT_NOT_EDITABLE              = "PROMPT_VERSION_CONTENT_NOT_EDITABLE";
    public static final String ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED      = "ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED";
    public static final String INVALID_PROMPT_VERSION_STATUS                    = "INVALID_PROMPT_VERSION_STATUS";
    public static final String INVALID_PROMPT_CONTENT_FORMAT                    = "INVALID_PROMPT_CONTENT_FORMAT";
    public static final String INVALID_PROMPT_CONTENT_JSON                      = "INVALID_PROMPT_CONTENT_JSON";

    private AiAgentErrorCodes() {}
}
