package com.company.scopery.modules.aiagent.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiAgentErrorCatalog implements ErrorCatalog {

    // ── Provider ─────────────────────────────────────────────────────────────

    PROVIDER_NOT_FOUND(
            "PROVIDER_NOT_FOUND",
            "Provider not found",
            HttpStatus.NOT_FOUND),

    PROVIDER_CODE_ALREADY_EXISTS(
            "PROVIDER_CODE_ALREADY_EXISTS",
            "Provider code already exists",
            HttpStatus.CONFLICT),

    DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED(
            "DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED",
            "Deprecated provider cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ACTIVE_PROVIDER_REQUIRES_API_BASE_URL(
            "ACTIVE_PROVIDER_REQUIRES_API_BASE_URL",
            "Provider cannot be activated without an API base URL",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_PROVIDER_STATUS(
            "INVALID_PROVIDER_STATUS",
            "Invalid provider status",
            HttpStatus.BAD_REQUEST),

    INVALID_PROVIDER_TYPE(
            "INVALID_PROVIDER_TYPE",
            "Invalid provider type",
            HttpStatus.BAD_REQUEST),

    // ── AI Model ─────────────────────────────────────────────────────────────

    AI_MODEL_NOT_FOUND(
            "AI_MODEL_NOT_FOUND",
            "AI model not found",
            HttpStatus.NOT_FOUND),

    AI_MODEL_CODE_ALREADY_EXISTS(
            "AI_MODEL_CODE_ALREADY_EXISTS",
            "AI model code already exists under this provider",
            HttpStatus.CONFLICT),

    AI_MODEL_PROVIDER_NOT_FOUND(
            "AI_MODEL_PROVIDER_NOT_FOUND",
            "Provider not found for this AI model",
            HttpStatus.NOT_FOUND),

    AI_MODEL_PROVIDER_NOT_ACTIVE(
            "AI_MODEL_PROVIDER_NOT_ACTIVE",
            "AI model provider is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED(
            "DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED",
            "Deprecated AI model cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_AI_MODEL_TYPE(
            "INVALID_AI_MODEL_TYPE",
            "Invalid AI model type",
            HttpStatus.BAD_REQUEST),

    INVALID_AI_MODEL_STATUS(
            "INVALID_AI_MODEL_STATUS",
            "Invalid AI model status",
            HttpStatus.BAD_REQUEST),

    // ── Model Deployment ─────────────────────────────────────────────────────

    MODEL_DEPLOYMENT_NOT_FOUND(
            "MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found",
            HttpStatus.NOT_FOUND),

    MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS(
            "MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS",
            "Deployment code already exists under this model",
            HttpStatus.CONFLICT),

    MODEL_DEPLOYMENT_MODEL_NOT_FOUND(
            "MODEL_DEPLOYMENT_MODEL_NOT_FOUND",
            "AI model not found for this deployment",
            HttpStatus.NOT_FOUND),

    MODEL_DEPLOYMENT_MODEL_NOT_ACTIVE(
            "MODEL_DEPLOYMENT_MODEL_NOT_ACTIVE",
            "Linked AI model is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MODEL_DEPLOYMENT_NOT_ACTIVE(
            "MODEL_DEPLOYMENT_NOT_ACTIVE",
            "Model deployment is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED(
            "DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED",
            "Deprecated model deployment cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MODEL_DEPLOYMENT_DEFAULT_CONFLICT(
            "MODEL_DEPLOYMENT_DEFAULT_CONFLICT",
            "A default deployment conflict occurred",
            HttpStatus.CONFLICT),

    INVALID_MODEL_DEPLOYMENT_ENVIRONMENT(
            "INVALID_MODEL_DEPLOYMENT_ENVIRONMENT",
            "Invalid model deployment environment",
            HttpStatus.BAD_REQUEST),

    INVALID_MODEL_DEPLOYMENT_STATUS(
            "INVALID_MODEL_DEPLOYMENT_STATUS",
            "Invalid model deployment status",
            HttpStatus.BAD_REQUEST),

    // ── Model Parameter Capability ────────────────────────────────────────────

    MODEL_PARAMETER_CAPABILITY_NOT_FOUND(
            "MODEL_PARAMETER_CAPABILITY_NOT_FOUND",
            "Model parameter capability not found",
            HttpStatus.NOT_FOUND),

    MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS(
            "MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS",
            "Parameter capability already exists for this model",
            HttpStatus.CONFLICT),

    MODEL_PARAMETER_CAPABILITY_MODEL_NOT_FOUND(
            "MODEL_PARAMETER_CAPABILITY_MODEL_NOT_FOUND",
            "AI model not found for this parameter capability",
            HttpStatus.NOT_FOUND),

    MODEL_PARAMETER_CAPABILITY_MODEL_DEPRECATED(
            "MODEL_PARAMETER_CAPABILITY_MODEL_DEPRECATED",
            "Linked AI model is deprecated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_MODEL_PARAMETER_SUPPORT_STATUS(
            "INVALID_MODEL_PARAMETER_SUPPORT_STATUS",
            "Invalid model parameter support status",
            HttpStatus.BAD_REQUEST),

    INVALID_MODEL_PARAMETER_VALUE_TYPE(
            "INVALID_MODEL_PARAMETER_VALUE_TYPE",
            "Invalid model parameter value type",
            HttpStatus.BAD_REQUEST),

    INVALID_MODEL_PARAMETER_RANGE(
            "INVALID_MODEL_PARAMETER_RANGE",
            "Invalid model parameter min/max range",
            HttpStatus.BAD_REQUEST),

    INVALID_MODEL_PARAMETER_NULLABLE_RULE(
            "INVALID_MODEL_PARAMETER_NULLABLE_RULE",
            "Nullable parameter requires ifNullBehavior to be set",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR(
            "INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR",
            "Invalid ifNullBehavior value",
            HttpStatus.BAD_REQUEST),

    INVALID_MODEL_PARAMETER_CAPABILITY_STATUS(
            "INVALID_MODEL_PARAMETER_CAPABILITY_STATUS",
            "Invalid model parameter capability status",
            HttpStatus.BAD_REQUEST),

    // ── Agent ─────────────────────────────────────────────────────────────────

    AGENT_NOT_FOUND(
            "AGENT_NOT_FOUND",
            "Agent not found",
            HttpStatus.NOT_FOUND),

    AGENT_CODE_ALREADY_EXISTS(
            "AGENT_CODE_ALREADY_EXISTS",
            "Agent code already exists",
            HttpStatus.CONFLICT),

    AGENT_DEFAULT_DEPLOYMENT_NOT_FOUND(
            "AGENT_DEFAULT_DEPLOYMENT_NOT_FOUND",
            "Default model deployment not found",
            HttpStatus.NOT_FOUND),

    AGENT_DEFAULT_DEPLOYMENT_NOT_ACTIVE(
            "AGENT_DEFAULT_DEPLOYMENT_NOT_ACTIVE",
            "Default model deployment must be ACTIVE",
            HttpStatus.UNPROCESSABLE_ENTITY),

    DEPRECATED_AGENT_CANNOT_BE_ACTIVATED(
            "DEPRECATED_AGENT_CANNOT_BE_ACTIVATED",
            "Deprecated agent cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_AGENT_TYPE(
            "INVALID_AGENT_TYPE",
            "Invalid agent type",
            HttpStatus.BAD_REQUEST),

    INVALID_AGENT_STATUS(
            "INVALID_AGENT_STATUS",
            "Invalid agent status",
            HttpStatus.BAD_REQUEST),

    INVALID_AGENT_OUTPUT_FORMAT(
            "INVALID_AGENT_OUTPUT_FORMAT",
            "Invalid agent output format",
            HttpStatus.BAD_REQUEST),

    // ── Prompt Template ───────────────────────────────────────────────────────

    PROMPT_TEMPLATE_NOT_FOUND(
            "PROMPT_TEMPLATE_NOT_FOUND",
            "Prompt template not found",
            HttpStatus.NOT_FOUND),

    PROMPT_TEMPLATE_CODE_ALREADY_EXISTS(
            "PROMPT_TEMPLATE_CODE_ALREADY_EXISTS",
            "Prompt template code already exists under this agent",
            HttpStatus.CONFLICT),

    PROMPT_TEMPLATE_AGENT_NOT_FOUND(
            "PROMPT_TEMPLATE_AGENT_NOT_FOUND",
            "Agent not found for this prompt template",
            HttpStatus.NOT_FOUND),

    PROMPT_TEMPLATE_AGENT_NOT_ACTIVE(
            "PROMPT_TEMPLATE_AGENT_NOT_ACTIVE",
            "Linked agent is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROMPT_TEMPLATE_AGENT_DEPRECATED(
            "PROMPT_TEMPLATE_AGENT_DEPRECATED",
            "Linked agent is deprecated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED(
            "DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED",
            "Deprecated prompt template cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_PROMPT_TEMPLATE_STATUS(
            "INVALID_PROMPT_TEMPLATE_STATUS",
            "Invalid prompt template status",
            HttpStatus.BAD_REQUEST),

    // ── Prompt Version ────────────────────────────────────────────────────────

    PROMPT_VERSION_NOT_FOUND(
            "PROMPT_VERSION_NOT_FOUND",
            "Prompt version not found",
            HttpStatus.NOT_FOUND),

    PROMPT_VERSION_NUMBER_CONFLICT(
            "PROMPT_VERSION_NUMBER_CONFLICT",
            "Prompt version number was just taken by a concurrent request; retry",
            HttpStatus.CONFLICT),

    PROMPT_VERSION_TEMPLATE_NOT_FOUND(
            "PROMPT_VERSION_TEMPLATE_NOT_FOUND",
            "Prompt template not found for this version",
            HttpStatus.NOT_FOUND),

    PROMPT_VERSION_TEMPLATE_NOT_ACTIVE(
            "PROMPT_VERSION_TEMPLATE_NOT_ACTIVE",
            "Linked prompt template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROMPT_VERSION_TEMPLATE_DEPRECATED(
            "PROMPT_VERSION_TEMPLATE_DEPRECATED",
            "Linked prompt template is deprecated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROMPT_VERSION_CONTENT_NOT_EDITABLE(
            "PROMPT_VERSION_CONTENT_NOT_EDITABLE",
            "Prompt version can only be edited while in DRAFT status",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED(
            "ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED",
            "Archived prompt version cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROMPT_VERSION_ALREADY_ARCHIVED(
            "PROMPT_VERSION_ALREADY_ARCHIVED",
            "Prompt version is already archived",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_PROMPT_VERSION_STATUS(
            "INVALID_PROMPT_VERSION_STATUS",
            "Invalid prompt version status",
            HttpStatus.BAD_REQUEST),

    INVALID_PROMPT_CONTENT_FORMAT(
            "INVALID_PROMPT_CONTENT_FORMAT",
            "Invalid prompt content format",
            HttpStatus.BAD_REQUEST),

    INVALID_PROMPT_CONTENT_JSON(
            "INVALID_PROMPT_CONTENT_JSON",
            "Prompt content is not valid JSON",
            HttpStatus.BAD_REQUEST),

    // ── Event Config ──────────────────────────────────────────────────────────

    EVENT_CONFIG_NOT_FOUND(
            "EVENT_CONFIG_NOT_FOUND",
            "Event configuration not found",
            HttpStatus.NOT_FOUND),

    ACTIVE_EVENT_CONFIG_NOT_FOUND(
            "ACTIVE_EVENT_CONFIG_NOT_FOUND",
            "No active event configuration found for this event and environment",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_RESOLVE_IDENTIFIER_REQUIRED(
            "EVENT_CONFIG_RESOLVE_IDENTIFIER_REQUIRED",
            "Either eventDefinitionId or both sourceSystem and eventKey are required",
            HttpStatus.BAD_REQUEST),

    EVENT_CONFIG_CODE_ALREADY_EXISTS(
            "EVENT_CONFIG_CODE_ALREADY_EXISTS",
            "Event configuration code already exists",
            HttpStatus.CONFLICT),

    EVENT_CONFIG_ACTIVE_ALREADY_EXISTS(
            "EVENT_CONFIG_ACTIVE_ALREADY_EXISTS",
            "An active event configuration already exists for this event definition and environment",
            HttpStatus.CONFLICT),

    DEPRECATED_EVENT_CONFIG_CANNOT_BE_ACTIVATED(
            "DEPRECATED_EVENT_CONFIG_CANNOT_BE_ACTIVATED",
            "Deprecated event configuration cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_EVENT_DEFINITION_NOT_FOUND(
            "EVENT_CONFIG_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found for this event configuration",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_EVENT_DEFINITION_NOT_ACTIVE(
            "EVENT_CONFIG_EVENT_DEFINITION_NOT_ACTIVE",
            "Linked event definition is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_AGENT_NOT_FOUND(
            "EVENT_CONFIG_AGENT_NOT_FOUND",
            "Agent not found for this event configuration",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_AGENT_NOT_ACTIVE(
            "EVENT_CONFIG_AGENT_NOT_ACTIVE",
            "Linked agent is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_PROMPT_VERSION_NOT_FOUND(
            "EVENT_CONFIG_PROMPT_VERSION_NOT_FOUND",
            "Prompt version not found for this event configuration",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_PROMPT_VERSION_NOT_ACTIVE(
            "EVENT_CONFIG_PROMPT_VERSION_NOT_ACTIVE",
            "Linked prompt version is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_PROMPT_TEMPLATE_NOT_FOUND(
            "EVENT_CONFIG_PROMPT_TEMPLATE_NOT_FOUND",
            "Prompt template not found for this event configuration",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_PROMPT_TEMPLATE_NOT_ACTIVE(
            "EVENT_CONFIG_PROMPT_TEMPLATE_NOT_ACTIVE",
            "Linked prompt template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_PROMPT_TEMPLATE_AGENT_MISMATCH(
            "EVENT_CONFIG_PROMPT_TEMPLATE_AGENT_MISMATCH",
            "Prompt template does not belong to the selected agent",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_FOUND(
            "EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found for this event configuration",
            HttpStatus.NOT_FOUND),

    EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_ACTIVE(
            "EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_ACTIVE",
            "Linked model deployment is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EVENT_CONFIG_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH(
            "EVENT_CONFIG_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH",
            "Model deployment environment does not match the event configuration environment",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_EVENT_CONFIG_STATUS(
            "INVALID_EVENT_CONFIG_STATUS",
            "Invalid event configuration status",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_CONFIG_TRIGGER_TYPE(
            "INVALID_EVENT_CONFIG_TRIGGER_TYPE",
            "Invalid event configuration trigger type",
            HttpStatus.BAD_REQUEST),

    INVALID_EVENT_CONFIG_ENVIRONMENT(
            "INVALID_EVENT_CONFIG_ENVIRONMENT",
            "Invalid event configuration environment",
            HttpStatus.BAD_REQUEST),

    // ── Usage Policy ──────────────────────────────────────────────────────────

    USAGE_POLICY_NOT_FOUND(
            "USAGE_POLICY_NOT_FOUND",
            "Usage policy not found",
            HttpStatus.NOT_FOUND),

    USAGE_POLICY_CODE_ALREADY_EXISTS(
            "USAGE_POLICY_CODE_ALREADY_EXISTS",
            "Usage policy code already exists",
            HttpStatus.CONFLICT),

    USAGE_POLICY_ACTIVE_ALREADY_EXISTS(
            "USAGE_POLICY_ACTIVE_ALREADY_EXISTS",
            "An active usage policy already exists for this target",
            HttpStatus.CONFLICT),

    DEPRECATED_USAGE_POLICY_CANNOT_BE_ACTIVATED(
            "DEPRECATED_USAGE_POLICY_CANNOT_BE_ACTIVATED",
            "Deprecated usage policy cannot be activated again",
            HttpStatus.UNPROCESSABLE_ENTITY),

    USAGE_POLICY_TARGET_ID_REQUIRED(
            "USAGE_POLICY_TARGET_ID_REQUIRED",
            "Target ID is required for the selected target type",
            HttpStatus.BAD_REQUEST),

    USAGE_POLICY_TARGET_ID_MUST_BE_NULL(
            "USAGE_POLICY_TARGET_ID_MUST_BE_NULL",
            "Target ID must be null for GLOBAL usage policies",
            HttpStatus.BAD_REQUEST),

    USAGE_POLICY_NO_LIMIT_DEFINED(
            "USAGE_POLICY_NO_LIMIT_DEFINED",
            "At least one usage limit must be defined",
            HttpStatus.UNPROCESSABLE_ENTITY),

    USAGE_POLICY_PERIOD_REQUIRED(
            "USAGE_POLICY_PERIOD_REQUIRED",
            "Period is required when rate-based limits are specified",
            HttpStatus.UNPROCESSABLE_ENTITY),

    USAGE_POLICY_EVENT_CONFIG_NOT_FOUND(
            "USAGE_POLICY_EVENT_CONFIG_NOT_FOUND",
            "Event configuration not found for this usage policy target",
            HttpStatus.NOT_FOUND),

    USAGE_POLICY_AGENT_NOT_FOUND(
            "USAGE_POLICY_AGENT_NOT_FOUND",
            "Agent not found for this usage policy target",
            HttpStatus.NOT_FOUND),

    USAGE_POLICY_MODEL_DEPLOYMENT_NOT_FOUND(
            "USAGE_POLICY_MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found for this usage policy target",
            HttpStatus.NOT_FOUND),

    INVALID_USAGE_POLICY_TARGET_TYPE(
            "INVALID_USAGE_POLICY_TARGET_TYPE",
            "Invalid usage policy target type",
            HttpStatus.BAD_REQUEST),

    INVALID_USAGE_POLICY_PERIOD(
            "INVALID_USAGE_POLICY_PERIOD",
            "Invalid usage policy period",
            HttpStatus.BAD_REQUEST),

    INVALID_USAGE_POLICY_ACTION(
            "INVALID_USAGE_POLICY_ACTION",
            "Invalid usage policy action",
            HttpStatus.BAD_REQUEST),

    INVALID_USAGE_POLICY_STATUS(
            "INVALID_USAGE_POLICY_STATUS",
            "Invalid usage policy status",
            HttpStatus.BAD_REQUEST),

    // ── Execution Log ─────────────────────────────────────────────────────────

    EXECUTION_LOG_NOT_FOUND(
            "EXECUTION_LOG_NOT_FOUND",
            "Execution log not found",
            HttpStatus.NOT_FOUND),

    EXECUTION_LOG_REQUEST_ID_ALREADY_EXISTS(
            "EXECUTION_LOG_REQUEST_ID_ALREADY_EXISTS",
            "Execution log with this requestId already exists",
            HttpStatus.CONFLICT),

    INVALID_EXECUTION_STATUS(
            "INVALID_EXECUTION_STATUS",
            "Invalid execution status",
            HttpStatus.BAD_REQUEST),

    INVALID_EXECUTION_TRIGGER_SOURCE(
            "INVALID_EXECUTION_TRIGGER_SOURCE",
            "Invalid execution trigger source",
            HttpStatus.BAD_REQUEST),

    INVALID_EXECUTION_STATUS_TRANSITION(
            "INVALID_EXECUTION_STATUS_TRANSITION",
            "Invalid execution status transition",
            HttpStatus.UNPROCESSABLE_ENTITY),

    TERMINAL_EXECUTION_LOG_CANNOT_BE_UPDATED(
            "TERMINAL_EXECUTION_LOG_CANNOT_BE_UPDATED",
            "Terminal execution log cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_EXECUTION_USAGE_METRICS(
            "INVALID_EXECUTION_USAGE_METRICS",
            "Invalid execution usage metrics",
            HttpStatus.BAD_REQUEST),

    INVALID_EXECUTION_METADATA_JSON(
            "INVALID_EXECUTION_METADATA_JSON",
            "Execution metadata is not valid JSON",
            HttpStatus.BAD_REQUEST),

    EXECUTION_LOG_EVENT_CONFIG_NOT_FOUND(
            "EXECUTION_LOG_EVENT_CONFIG_NOT_FOUND",
            "Event configuration not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_LOG_EVENT_DEFINITION_NOT_FOUND(
            "EXECUTION_LOG_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_LOG_AGENT_NOT_FOUND(
            "EXECUTION_LOG_AGENT_NOT_FOUND",
            "Agent not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_LOG_PROMPT_VERSION_NOT_FOUND(
            "EXECUTION_LOG_PROMPT_VERSION_NOT_FOUND",
            "Prompt version not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_LOG_MODEL_DEPLOYMENT_NOT_FOUND(
            "EXECUTION_LOG_MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found for this execution",
            HttpStatus.NOT_FOUND),

    // ── Execution (AI Provider Integration) ──────────────────────────────────

    EXECUTION_EVENT_CONFIG_NOT_FOUND(
            "EXECUTION_EVENT_CONFIG_NOT_FOUND",
            "No active event configuration found for this event and environment",
            HttpStatus.NOT_FOUND),

    EXECUTION_EVENT_CONFIG_NOT_ACTIVE(
            "EXECUTION_EVENT_CONFIG_NOT_ACTIVE",
            "Event configuration is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_EVENT_DEFINITION_NOT_FOUND(
            "EXECUTION_EVENT_DEFINITION_NOT_FOUND",
            "Event definition not found",
            HttpStatus.NOT_FOUND),

    EXECUTION_EVENT_DEFINITION_NOT_ACTIVE(
            "EXECUTION_EVENT_DEFINITION_NOT_ACTIVE",
            "Event definition is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_AGENT_NOT_FOUND(
            "EXECUTION_AGENT_NOT_FOUND",
            "Agent not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_AGENT_NOT_ACTIVE(
            "EXECUTION_AGENT_NOT_ACTIVE",
            "Agent is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_PROMPT_TEMPLATE_NOT_FOUND(
            "EXECUTION_PROMPT_TEMPLATE_NOT_FOUND",
            "Prompt template not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_PROMPT_TEMPLATE_NOT_ACTIVE(
            "EXECUTION_PROMPT_TEMPLATE_NOT_ACTIVE",
            "Prompt template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_PROMPT_TEMPLATE_AGENT_MISMATCH(
            "EXECUTION_PROMPT_TEMPLATE_AGENT_MISMATCH",
            "Prompt template does not belong to the agent linked in this event configuration",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_PROMPT_VERSION_NOT_FOUND(
            "EXECUTION_PROMPT_VERSION_NOT_FOUND",
            "Prompt version not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_PROMPT_VERSION_NOT_ACTIVE(
            "EXECUTION_PROMPT_VERSION_NOT_ACTIVE",
            "Prompt version is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_MODEL_DEPLOYMENT_NOT_FOUND(
            "EXECUTION_MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_MODEL_DEPLOYMENT_NOT_ACTIVE(
            "EXECUTION_MODEL_DEPLOYMENT_NOT_ACTIVE",
            "Model deployment is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH(
            "EXECUTION_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH",
            "Model deployment environment does not match the runtime environment",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_AI_MODEL_NOT_FOUND(
            "EXECUTION_AI_MODEL_NOT_FOUND",
            "AI model not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_AI_MODEL_NOT_ACTIVE(
            "EXECUTION_AI_MODEL_NOT_ACTIVE",
            "AI model is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    EXECUTION_PROVIDER_NOT_FOUND(
            "EXECUTION_PROVIDER_NOT_FOUND",
            "Provider not found for this execution",
            HttpStatus.NOT_FOUND),

    EXECUTION_PROVIDER_NOT_ACTIVE(
            "EXECUTION_PROVIDER_NOT_ACTIVE",
            "Provider is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROMPT_RENDER_VARIABLE_MISSING(
            "PROMPT_RENDER_VARIABLE_MISSING",
            "Required prompt variable is missing",
            HttpStatus.BAD_REQUEST),

    PROMPT_RENDER_FAILED(
            "PROMPT_RENDER_FAILED",
            "Prompt rendering failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROVIDER_ADAPTER_NOT_IMPLEMENTED(
            "PROVIDER_ADAPTER_NOT_IMPLEMENTED",
            "No AI provider adapter is implemented for this provider",
            HttpStatus.NOT_IMPLEMENTED),

    OPENAI_API_KEY_MISSING(
            "OPENAI_API_KEY_MISSING",
            "OpenAI API key is not configured",
            HttpStatus.INTERNAL_SERVER_ERROR),

    OPENAI_API_CALL_FAILED(
            "OPENAI_API_CALL_FAILED",
            "OpenAI API call failed",
            HttpStatus.BAD_GATEWAY),

    OPENAI_API_TIMEOUT(
            "OPENAI_API_TIMEOUT",
            "OpenAI API call timed out",
            HttpStatus.GATEWAY_TIMEOUT),

    OPENAI_API_RESPONSE_INVALID(
            "OPENAI_API_RESPONSE_INVALID",
            "OpenAI API returned an invalid response",
            HttpStatus.BAD_GATEWAY),

    // ── Provider Secret ───────────────────────────────────────────────────────

    PROVIDER_SECRET_NOT_FOUND(
            "PROVIDER_SECRET_NOT_FOUND",
            "Provider secret not found",
            HttpStatus.NOT_FOUND),

    PROVIDER_SECRET_PROVIDER_NOT_FOUND(
            "PROVIDER_SECRET_PROVIDER_NOT_FOUND",
            "Provider not found for this secret",
            HttpStatus.NOT_FOUND),

    PROVIDER_SECRET_PROVIDER_NOT_ACTIVE(
            "PROVIDER_SECRET_PROVIDER_NOT_ACTIVE",
            "Provider is not active — cannot set a secret on an inactive provider",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PROVIDER_SECRET_ACTIVE_ALREADY_EXISTS(
            "PROVIDER_SECRET_ACTIVE_ALREADY_EXISTS",
            "An active secret of this type already exists for this provider",
            HttpStatus.CONFLICT),

    PROVIDER_SECRET_MASTER_KEY_MISSING(
            "PROVIDER_SECRET_MASTER_KEY_MISSING",
            "Encryption master key is not configured",
            HttpStatus.INTERNAL_SERVER_ERROR),

    PROVIDER_SECRET_MASTER_KEY_INVALID(
            "PROVIDER_SECRET_MASTER_KEY_INVALID",
            "Encryption master key is invalid",
            HttpStatus.INTERNAL_SERVER_ERROR),

    PROVIDER_SECRET_ENCRYPTION_FAILED(
            "PROVIDER_SECRET_ENCRYPTION_FAILED",
            "Secret encryption failed",
            HttpStatus.INTERNAL_SERVER_ERROR),

    PROVIDER_SECRET_DECRYPTION_FAILED(
            "PROVIDER_SECRET_DECRYPTION_FAILED",
            "Secret decryption failed",
            HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_PROVIDER_SECRET_TYPE(
            "INVALID_PROVIDER_SECRET_TYPE",
            "Invalid provider secret type",
            HttpStatus.BAD_REQUEST),

    INVALID_PROVIDER_SECRET_STATUS(
            "INVALID_PROVIDER_SECRET_STATUS",
            "Invalid provider secret status",
            HttpStatus.BAD_REQUEST),

    // ── Usage Policy Evaluator ────────────────────────────────────────────────

    USAGE_POLICY_EXCEEDED(
            "USAGE_POLICY_EXCEEDED",
            "Usage policy limit exceeded",
            HttpStatus.TOO_MANY_REQUESTS),

    USAGE_POLICY_EVALUATION_FAILED(
            "USAGE_POLICY_EVALUATION_FAILED",
            "Usage policy evaluation failed unexpectedly",
            HttpStatus.INTERNAL_SERVER_ERROR),

    // ── Playground ────────────────────────────────────────────────────────────

    PLAYGROUND_EVENT_CONFIG_NOT_FOUND(
            "PLAYGROUND_EVENT_CONFIG_NOT_FOUND",
            "Event configuration not found for playground",
            HttpStatus.NOT_FOUND),

    PLAYGROUND_EVENT_CONFIG_NOT_ACTIVE(
            "PLAYGROUND_EVENT_CONFIG_NOT_ACTIVE",
            "Event configuration is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_AGENT_NOT_FOUND(
            "PLAYGROUND_AGENT_NOT_FOUND",
            "Agent not found for playground",
            HttpStatus.NOT_FOUND),

    PLAYGROUND_AGENT_NOT_ACTIVE(
            "PLAYGROUND_AGENT_NOT_ACTIVE",
            "Agent is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_PROMPT_VERSION_NOT_FOUND(
            "PLAYGROUND_PROMPT_VERSION_NOT_FOUND",
            "Prompt version not found for playground",
            HttpStatus.NOT_FOUND),

    PLAYGROUND_PROMPT_VERSION_NOT_ACTIVE(
            "PLAYGROUND_PROMPT_VERSION_NOT_ACTIVE",
            "Prompt version is not active or not in DRAFT status",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_PROMPT_TEMPLATE_NOT_FOUND(
            "PLAYGROUND_PROMPT_TEMPLATE_NOT_FOUND",
            "Prompt template not found for playground",
            HttpStatus.NOT_FOUND),

    PLAYGROUND_PROMPT_TEMPLATE_NOT_ACTIVE(
            "PLAYGROUND_PROMPT_TEMPLATE_NOT_ACTIVE",
            "Prompt template is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_PROMPT_TEMPLATE_AGENT_MISMATCH(
            "PLAYGROUND_PROMPT_TEMPLATE_AGENT_MISMATCH",
            "Prompt template does not belong to the selected agent",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_MODEL_DEPLOYMENT_NOT_FOUND(
            "PLAYGROUND_MODEL_DEPLOYMENT_NOT_FOUND",
            "Model deployment not found for playground",
            HttpStatus.NOT_FOUND),

    PLAYGROUND_MODEL_DEPLOYMENT_NOT_ACTIVE(
            "PLAYGROUND_MODEL_DEPLOYMENT_NOT_ACTIVE",
            "Model deployment is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_RUN_FAILED(
            "PLAYGROUND_RUN_FAILED",
            "Playground run failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_PROMPT_PREVIEW_FAILED(
            "PLAYGROUND_PROMPT_PREVIEW_FAILED",
            "Prompt preview failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    PLAYGROUND_DISABLED_IN_ENVIRONMENT(
            "PLAYGROUND_DISABLED_IN_ENVIRONMENT",
            "Playground direct execution is disabled in this environment",
            HttpStatus.FORBIDDEN),

    // ── Execution Lifecycle ───────────────────────────────────────────────────

    EXECUTION_LIFECYCLE_WRITE_RESTRICTED(
            "EXECUTION_LIFECYCLE_WRITE_RESTRICTED",
            "Execution lifecycle writes are restricted to internal services only",
            HttpStatus.FORBIDDEN),

    AI_INVALID_INPUT_VARIABLES(
            "AI_INVALID_INPUT_VARIABLES",
            "AI execution input does not match the approved schema",
            HttpStatus.BAD_REQUEST),

    AI_INVALID_OUTPUT(
            "AI_INVALID_OUTPUT",
            "AI output does not match the approved schema",
            HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiAgentErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
