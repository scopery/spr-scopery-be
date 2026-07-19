package com.company.scopery.modules.aiagent.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiAgentExceptions {

    private AiAgentExceptions() {}

    // ── Provider ─────────────────────────────────────────────────────────────

    public static AppException providerNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_NOT_FOUND,
                "Provider not found: " + id, Map.of("id", id));
    }

    public static AppException providerCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_CODE_ALREADY_EXISTS,
                "Provider code already exists: " + code, Map.of("code", code));
    }

    public static AppException deprecatedProviderCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED,
                "Deprecated provider cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException providerRequiresApiBaseUrl(String code) {
        return new AppException(AiAgentErrorCatalog.ACTIVE_PROVIDER_REQUIRES_API_BASE_URL,
                "Provider cannot be activated without an API base URL: " + code, Map.of("code", code));
    }

    public static AppException providerHasActiveDeployments(UUID providerId) {
        return new AppException(AiAgentErrorCatalog.AI_PROVIDER_HAS_ACTIVE_DEPLOYMENTS,
                "Provider cannot be deactivated while active model deployments exist: " + providerId,
                Map.of("providerId", providerId));
    }

    // ── AI Model ─────────────────────────────────────────────────────────────

    public static AppException aiModelNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.AI_MODEL_NOT_FOUND,
                "AI model not found: " + id, Map.of("id", id));
    }

    public static AppException aiModelCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.AI_MODEL_CODE_ALREADY_EXISTS,
                "AI model code already exists under this provider: " + code, Map.of("code", code));
    }

    public static AppException aiModelProviderNotFound(UUID providerId) {
        return new AppException(AiAgentErrorCatalog.AI_MODEL_PROVIDER_NOT_FOUND,
                "Provider not found: " + providerId, Map.of("providerId", providerId));
    }

    public static AppException aiModelProviderNotActive(String providerCode) {
        return new AppException(AiAgentErrorCatalog.AI_MODEL_PROVIDER_NOT_ACTIVE,
                "Cannot create/activate model under a non-active provider: " + providerCode,
                Map.of("providerCode", providerCode));
    }

    public static AppException deprecatedAiModelCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED,
                "Deprecated AI model cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException aiModelHasActiveDeployments(UUID modelId) {
        return new AppException(AiAgentErrorCatalog.AI_MODEL_HAS_ACTIVE_DEPLOYMENTS,
                "AI model cannot be deactivated while active deployments exist: " + modelId,
                Map.of("modelId", modelId));
    }

    // ── Model Deployment ─────────────────────────────────────────────────────

    public static AppException modelDeploymentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found: " + id, Map.of("id", id));
    }

    public static AppException modelDeploymentCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.MODEL_DEPLOYMENT_CODE_ALREADY_EXISTS,
                "Deployment code already exists under this model: " + code, Map.of("code", code));
    }

    public static AppException modelDeploymentModelNotFound(UUID modelId) {
        return new AppException(AiAgentErrorCatalog.MODEL_DEPLOYMENT_MODEL_NOT_FOUND,
                "AI model not found: " + modelId, Map.of("modelId", modelId));
    }

    public static AppException modelDeploymentModelNotActive(String modelCode) {
        return new AppException(AiAgentErrorCatalog.MODEL_DEPLOYMENT_MODEL_NOT_ACTIVE,
                "Cannot create/activate deployment under a non-active AI model: " + modelCode,
                Map.of("modelCode", modelCode));
    }

    public static AppException modelDeploymentNotActive(String code) {
        return new AppException(AiAgentErrorCatalog.MODEL_DEPLOYMENT_NOT_ACTIVE,
                "Only ACTIVE deployments can be set as default, current: " + code, Map.of("code", code));
    }

    public static AppException deprecatedModelDeploymentCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_MODEL_DEPLOYMENT_CANNOT_BE_ACTIVATED,
                "Deprecated model deployment cannot be activated again: " + code, Map.of("code", code));
    }

    // ── Model Parameter Capability ────────────────────────────────────────────

    public static AppException modelParameterCapabilityNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_NOT_FOUND,
                "Model parameter capability not found: " + id, Map.of("id", id));
    }

    public static AppException modelParameterCapabilityAlreadyExists(String parameterName) {
        return new AppException(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_ALREADY_EXISTS,
                "Parameter capability already exists for this model: " + parameterName,
                Map.of("parameterName", parameterName));
    }

    public static AppException modelParameterCapabilityModelNotFound(UUID modelId) {
        return new AppException(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_MODEL_NOT_FOUND,
                "AI model not found: " + modelId, Map.of("modelId", modelId));
    }

    public static AppException modelParameterCapabilityModelDeprecated(String modelCode) {
        return new AppException(AiAgentErrorCatalog.MODEL_PARAMETER_CAPABILITY_MODEL_DEPRECATED,
                "Cannot operate on capability — linked AI model is deprecated: " + modelCode,
                Map.of("modelCode", modelCode));
    }

    // ── Agent ─────────────────────────────────────────────────────────────────

    public static AppException agentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.AGENT_NOT_FOUND,
                "Agent not found: " + id, Map.of("id", id));
    }

    public static AppException agentCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.AGENT_CODE_ALREADY_EXISTS,
                "Agent code already exists: " + code, Map.of("code", code));
    }

    public static AppException agentDefaultDeploymentNotFound(UUID deploymentId) {
        return new AppException(AiAgentErrorCatalog.AGENT_DEFAULT_DEPLOYMENT_NOT_FOUND,
                "Default model deployment not found: " + deploymentId, Map.of("deploymentId", deploymentId));
    }

    public static AppException agentDefaultDeploymentNotActive(String currentStatus) {
        return new AppException(AiAgentErrorCatalog.AGENT_DEFAULT_DEPLOYMENT_NOT_ACTIVE,
                "Default model deployment must be ACTIVE, but is: " + currentStatus,
                Map.of("currentStatus", currentStatus));
    }

    public static AppException deprecatedAgentCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_AGENT_CANNOT_BE_ACTIVATED,
                "Deprecated agent cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException agentAutonomyNotAllowed(String autonomyLevel) {
        return new AppException(AiAgentErrorCatalog.AI_AGENT_AUTONOMY_NOT_ALLOWED,
                "Agent autonomy level not allowed in Phase 07 (no business mutation): " + autonomyLevel,
                Map.of("autonomyLevel", autonomyLevel));
    }

    public static AppException executionPolicyBlocked(String reasonCode) {
        return new AppException(AiAgentErrorCatalog.AI_EXECUTION_POLICY_BLOCKED,
                "AI execution blocked by usage policy: " + reasonCode,
                Map.of("reasonCode", reasonCode == null ? "" : reasonCode));
    }

    // ── Prompt Template ───────────────────────────────────────────────────────

    public static AppException promptTemplateNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PROMPT_TEMPLATE_NOT_FOUND,
                "Prompt template not found: " + id, Map.of("id", id));
    }

    public static AppException promptTemplateCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.PROMPT_TEMPLATE_CODE_ALREADY_EXISTS,
                "Prompt template code already exists under this agent: " + code, Map.of("code", code));
    }

    public static AppException promptTemplateAgentNotFound(UUID agentId) {
        return new AppException(AiAgentErrorCatalog.PROMPT_TEMPLATE_AGENT_NOT_FOUND,
                "Agent not found: " + agentId, Map.of("agentId", agentId));
    }

    public static AppException promptTemplateAgentNotActive(String agentCode) {
        return new AppException(AiAgentErrorCatalog.PROMPT_TEMPLATE_AGENT_NOT_ACTIVE,
                "Cannot activate prompt template — linked agent is not ACTIVE: " + agentCode,
                Map.of("agentCode", agentCode));
    }

    public static AppException promptTemplateAgentDeprecated(String agentCode) {
        return new AppException(AiAgentErrorCatalog.PROMPT_TEMPLATE_AGENT_DEPRECATED,
                "Cannot create prompt template under a deprecated agent: " + agentCode,
                Map.of("agentCode", agentCode));
    }

    public static AppException deprecatedPromptTemplateCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_PROMPT_TEMPLATE_CANNOT_BE_ACTIVATED,
                "Deprecated prompt template cannot be activated again: " + code, Map.of("code", code));
    }

    // ── Prompt Version ────────────────────────────────────────────────────────

    public static AppException promptVersionNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_NOT_FOUND,
                "Prompt version not found: " + id, Map.of("id", id));
    }

    public static AppException promptVersionTemplateNotFound(UUID templateId) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_TEMPLATE_NOT_FOUND,
                "Prompt template not found: " + templateId, Map.of("templateId", templateId));
    }

    public static AppException promptVersionTemplateNotActive(String templateCode) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_TEMPLATE_NOT_ACTIVE,
                "Cannot activate prompt version — linked template is not ACTIVE: " + templateCode,
                Map.of("templateCode", templateCode));
    }

    public static AppException promptVersionNumberConflict(UUID templateId) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_NUMBER_CONFLICT,
                "Prompt version number was just taken by a concurrent request for template "
                        + templateId + "; retry", Map.of("templateId", templateId));
    }

    public static AppException promptVersionTemplateDeprecated(String templateCode) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_TEMPLATE_DEPRECATED,
                "Cannot create prompt version under a deprecated template: " + templateCode,
                Map.of("templateCode", templateCode));
    }

    public static AppException promptVersionContentNotEditable(String currentStatus) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_CONTENT_NOT_EDITABLE,
                "Prompt version can only be edited while in DRAFT status, current: " + currentStatus,
                Map.of("currentStatus", currentStatus));
    }

    public static AppException archivedPromptVersionCannotBeActivated(int versionNumber) {
        return new AppException(AiAgentErrorCatalog.ARCHIVED_PROMPT_VERSION_CANNOT_BE_ACTIVATED,
                "Archived prompt version cannot be activated again: v" + versionNumber,
                Map.of("versionNumber", versionNumber));
    }

    public static AppException promptVersionAlreadyArchived(int versionNumber) {
        return new AppException(AiAgentErrorCatalog.PROMPT_VERSION_ALREADY_ARCHIVED,
                "Prompt version is already archived: v" + versionNumber,
                Map.of("versionNumber", versionNumber));
    }

    public static AppException invalidPromptContentJson() {
        return new AppException(AiAgentErrorCatalog.INVALID_PROMPT_CONTENT_JSON);
    }

    // ── Event Config ──────────────────────────────────────────────────────────

    public static AppException eventConfigNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_NOT_FOUND,
                "Event configuration not found: " + id, Map.of("id", id));
    }

    public static AppException activeEventConfigNotFound(UUID eventDefinitionId, String environment) {
        return new AppException(AiAgentErrorCatalog.ACTIVE_EVENT_CONFIG_NOT_FOUND,
                "No active event configuration for event definition " + eventDefinitionId
                        + " in environment " + environment,
                Map.of("eventDefinitionId", eventDefinitionId, "environment", environment));
    }

    public static AppException eventConfigResolveIdentifierRequired() {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_RESOLVE_IDENTIFIER_REQUIRED);
    }

    public static AppException eventConfigCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_CODE_ALREADY_EXISTS,
                "Event configuration code already exists: " + code, Map.of("code", code));
    }

    public static AppException eventConfigActiveAlreadyExists(UUID eventDefinitionId, String environment) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_ACTIVE_ALREADY_EXISTS,
                "An active event configuration already exists for event definition "
                        + eventDefinitionId + " in environment " + environment,
                Map.of("eventDefinitionId", eventDefinitionId, "environment", environment));
    }

    public static AppException deprecatedEventConfigCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_EVENT_CONFIG_CANNOT_BE_ACTIVATED,
                "Deprecated event configuration cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException eventConfigEventDefinitionNotFound(UUID eventDefinitionId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found: " + eventDefinitionId, Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException eventConfigEventDefinitionNotActive(UUID eventDefinitionId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_EVENT_DEFINITION_NOT_ACTIVE,
                "Linked event definition is not active: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException eventConfigAgentNotFound(UUID agentId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_AGENT_NOT_FOUND,
                "Agent not found: " + agentId, Map.of("agentId", agentId));
    }

    public static AppException eventConfigAgentNotActive(UUID agentId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_AGENT_NOT_ACTIVE,
                "Linked agent is not active: " + agentId, Map.of("agentId", agentId));
    }

    public static AppException eventConfigPromptVersionNotFound(UUID promptVersionId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_VERSION_NOT_FOUND,
                "Prompt version not found: " + promptVersionId, Map.of("promptVersionId", promptVersionId));
    }

    public static AppException eventConfigPromptVersionNotActive(UUID promptVersionId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_VERSION_NOT_ACTIVE,
                "Linked prompt version is not active: " + promptVersionId,
                Map.of("promptVersionId", promptVersionId));
    }

    public static AppException eventConfigPromptTemplateNotFound(UUID templateId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_NOT_FOUND,
                "Prompt template not found: " + templateId, Map.of("templateId", templateId));
    }

    public static AppException eventConfigPromptTemplateNotActive(UUID templateId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_NOT_ACTIVE,
                "Linked prompt template is not active: " + templateId, Map.of("templateId", templateId));
    }

    public static AppException eventConfigPromptTemplateAgentMismatch(UUID templateId, UUID agentId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_PROMPT_TEMPLATE_AGENT_MISMATCH,
                "Prompt template " + templateId + " does not belong to agent " + agentId,
                Map.of("templateId", templateId, "agentId", agentId));
    }

    public static AppException eventConfigModelDeploymentNotFound(UUID deploymentId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found: " + deploymentId, Map.of("deploymentId", deploymentId));
    }

    public static AppException eventConfigModelDeploymentNotActive(UUID deploymentId) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_NOT_ACTIVE,
                "Linked model deployment is not active: " + deploymentId, Map.of("deploymentId", deploymentId));
    }

    public static AppException eventConfigModelDeploymentEnvironmentMismatch(String deploymentEnv, String configEnv) {
        return new AppException(AiAgentErrorCatalog.EVENT_CONFIG_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH,
                "Model deployment environment '" + deploymentEnv
                        + "' does not match event config environment '" + configEnv + "'",
                Map.of("deploymentEnvironment", deploymentEnv, "configEnvironment", configEnv));
    }

    // ── Usage Policy ──────────────────────────────────────────────────────────

    public static AppException usagePolicyNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_NOT_FOUND,
                "Usage policy not found: " + id, Map.of("id", id));
    }

    public static AppException usagePolicyCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_CODE_ALREADY_EXISTS,
                "Usage policy code already exists: " + code, Map.of("code", code));
    }

    public static AppException usagePolicyActiveAlreadyExists(String targetType, UUID targetId) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_ACTIVE_ALREADY_EXISTS,
                "An active usage policy already exists for targetType=" + targetType
                        + (targetId != null ? " targetId=" + targetId : ""),
                targetId != null
                        ? Map.of("targetType", targetType, "targetId", targetId)
                        : Map.of("targetType", targetType));
    }

    public static AppException deprecatedUsagePolicyCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_USAGE_POLICY_CANNOT_BE_ACTIVATED,
                "Deprecated usage policy cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException usagePolicyTargetIdRequired(String targetType) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_TARGET_ID_REQUIRED,
                "Target ID is required for targetType=" + targetType, Map.of("targetType", targetType));
    }

    public static AppException usagePolicyTargetIdMustBeNull() {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_TARGET_ID_MUST_BE_NULL);
    }

    public static AppException usagePolicyNoLimitDefined() {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_NO_LIMIT_DEFINED);
    }

    public static AppException usagePolicyPeriodRequired() {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_PERIOD_REQUIRED);
    }

    public static AppException usagePolicyEventConfigNotFound(UUID eventConfigId) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_EVENT_CONFIG_NOT_FOUND,
                "Event configuration not found: " + eventConfigId, Map.of("eventConfigId", eventConfigId));
    }

    public static AppException usagePolicyAgentNotFound(UUID agentId) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_AGENT_NOT_FOUND,
                "Agent not found: " + agentId, Map.of("agentId", agentId));
    }

    public static AppException usagePolicyModelDeploymentNotFound(UUID deploymentId) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found: " + deploymentId, Map.of("deploymentId", deploymentId));
    }

    // ── Execution Log ─────────────────────────────────────────────────────────

    public static AppException executionLogNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_NOT_FOUND,
                "Execution log not found: " + id, Map.of("id", id));
    }

    public static AppException executionLogRequestIdAlreadyExists(String requestId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_REQUEST_ID_ALREADY_EXISTS,
                "Execution log with requestId already exists: " + requestId,
                Map.of("requestId", requestId));
    }

    public static AppException invalidExecutionStatusTransition(String from, String to) {
        return new AppException(AiAgentErrorCatalog.INVALID_EXECUTION_STATUS_TRANSITION,
                "Cannot transition execution status from " + from + " to " + to,
                Map.of("from", from, "to", to));
    }

    public static AppException terminalExecutionLogCannotBeUpdated(String status) {
        return new AppException(AiAgentErrorCatalog.TERMINAL_EXECUTION_LOG_CANNOT_BE_UPDATED,
                "Execution log in terminal status cannot be updated: " + status,
                Map.of("status", status));
    }

    public static AppException invalidExecutionUsageMetrics(String reason) {
        return new AppException(AiAgentErrorCatalog.INVALID_EXECUTION_USAGE_METRICS,
                "Invalid execution usage metrics: " + reason, Map.of("reason", reason));
    }

    public static AppException invalidExecutionMetadataJson() {
        return new AppException(AiAgentErrorCatalog.INVALID_EXECUTION_METADATA_JSON);
    }

    public static AppException executionLogEventConfigNotFound(UUID eventConfigId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_EVENT_CONFIG_NOT_FOUND,
                "Event configuration not found: " + eventConfigId,
                Map.of("eventConfigId", eventConfigId));
    }

    public static AppException executionLogEventDefinitionNotFound(UUID eventDefinitionId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found: " + eventDefinitionId,
                Map.of("eventDefinitionId", eventDefinitionId));
    }

    public static AppException executionLogAgentNotFound(UUID agentId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_AGENT_NOT_FOUND,
                "Agent not found: " + agentId, Map.of("agentId", agentId));
    }

    public static AppException executionLogPromptVersionNotFound(UUID promptVersionId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_PROMPT_VERSION_NOT_FOUND,
                "Prompt version not found: " + promptVersionId,
                Map.of("promptVersionId", promptVersionId));
    }

    public static AppException executionLogModelDeploymentNotFound(UUID deploymentId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LOG_MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found: " + deploymentId,
                Map.of("deploymentId", deploymentId));
    }

    // ── Execution (AI Provider Integration) ──────────────────────────────────

    public static AppException executionEventConfigNotFound(UUID eventDefinitionId, String environment) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_FOUND,
                "No active event configuration for event definition " + eventDefinitionId
                        + " in environment " + environment,
                Map.of("eventDefinitionId", eventDefinitionId, "environment", environment));
    }

    public static AppException executionEventConfigNotFoundById(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_FOUND,
                "Event configuration not found: " + id, Map.of("id", id));
    }

    public static AppException executionEventConfigNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_EVENT_CONFIG_NOT_ACTIVE,
                "Event configuration is not active: " + id, Map.of("id", id));
    }

    public static AppException executionEventDefinitionNotFound(String eventCode) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_EVENT_DEFINITION_NOT_FOUND,
                "Event definition not found for code: " + eventCode, Map.of("eventCode", eventCode));
    }

    public static AppException executionEventDefinitionNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_EVENT_DEFINITION_NOT_ACTIVE,
                "Event definition is not active: " + id, Map.of("id", id));
    }

    public static AppException executionAgentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_AGENT_NOT_FOUND,
                "Agent not found: " + id, Map.of("id", id));
    }

    public static AppException executionAgentNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_AGENT_NOT_ACTIVE,
                "Agent is not active: " + id, Map.of("id", id));
    }

    public static AppException executionPromptTemplateNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROMPT_TEMPLATE_NOT_FOUND,
                "Prompt template not found: " + id, Map.of("id", id));
    }

    public static AppException executionPromptTemplateNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROMPT_TEMPLATE_NOT_ACTIVE,
                "Prompt template is not active: " + id, Map.of("id", id));
    }

    public static AppException executionPromptTemplateAgentMismatch(UUID templateId, UUID agentId) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROMPT_TEMPLATE_AGENT_MISMATCH,
                "Prompt template " + templateId + " does not belong to agent " + agentId,
                Map.of("templateId", templateId, "agentId", agentId));
    }

    public static AppException executionPromptVersionNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROMPT_VERSION_NOT_FOUND,
                "Prompt version not found: " + id, Map.of("id", id));
    }

    public static AppException executionPromptVersionNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROMPT_VERSION_NOT_ACTIVE,
                "Prompt version is not active: " + id, Map.of("id", id));
    }

    public static AppException executionModelDeploymentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found: " + id, Map.of("id", id));
    }

    public static AppException executionModelDeploymentNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_NOT_ACTIVE,
                "Model deployment is not active: " + id, Map.of("id", id));
    }

    public static AppException executionModelDeploymentEnvironmentMismatch(String deploymentEnv, String runtimeEnv) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_MODEL_DEPLOYMENT_ENVIRONMENT_MISMATCH,
                "Model deployment environment '" + deploymentEnv
                        + "' does not match runtime environment '" + runtimeEnv + "'",
                Map.of("deploymentEnvironment", deploymentEnv, "runtimeEnvironment", runtimeEnv));
    }

    public static AppException executionAiModelNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_AI_MODEL_NOT_FOUND,
                "AI model not found: " + id, Map.of("id", id));
    }

    public static AppException executionAiModelNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_AI_MODEL_NOT_ACTIVE,
                "AI model is not active: " + id, Map.of("id", id));
    }

    public static AppException executionProviderNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROVIDER_NOT_FOUND,
                "Provider not found: " + id, Map.of("id", id));
    }

    public static AppException executionProviderNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.EXECUTION_PROVIDER_NOT_ACTIVE,
                "Provider is not active: " + id, Map.of("id", id));
    }

    public static AppException promptRenderVariableMissing(String variableName) {
        return new AppException(AiAgentErrorCatalog.PROMPT_RENDER_VARIABLE_MISSING,
                "Required prompt variable is missing: " + variableName,
                Map.of("variableName", variableName));
    }

    public static AppException promptRenderFailed(String reason) {
        return new AppException(AiAgentErrorCatalog.PROMPT_RENDER_FAILED,
                "Prompt rendering failed: " + reason, Map.of("reason", reason));
    }

    public static AppException providerAdapterNotImplemented(String providerCode) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_ADAPTER_NOT_IMPLEMENTED,
                "No AI provider adapter implemented for provider: " + providerCode,
                Map.of("providerCode", providerCode));
    }

    public static AppException openAiApiKeyMissing() {
        return new AppException(AiAgentErrorCatalog.OPENAI_API_KEY_MISSING);
    }

    public static AppException openAiApiCallFailed(int statusCode) {
        return new AppException(AiAgentErrorCatalog.OPENAI_API_CALL_FAILED,
                "OpenAI API call failed with status " + statusCode,
                Map.of("statusCode", statusCode));
    }

    public static AppException openAiApiTimeout() {
        return new AppException(AiAgentErrorCatalog.OPENAI_API_TIMEOUT);
    }

    public static AppException openAiApiResponseInvalid(String reason) {
        return new AppException(AiAgentErrorCatalog.OPENAI_API_RESPONSE_INVALID,
                "OpenAI API returned an invalid response: " + reason, Map.of("reason", reason));
    }

    // ── Provider Secret ───────────────────────────────────────────────────────

    public static AppException providerSecretNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_NOT_FOUND,
                "Provider secret not found: " + id, Map.of("id", id));
    }

    public static AppException providerSecretProviderNotFound(UUID providerId) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_PROVIDER_NOT_FOUND,
                "Provider not found: " + providerId, Map.of("providerId", providerId));
    }

    public static AppException providerSecretProviderNotActive(UUID providerId) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_PROVIDER_NOT_ACTIVE,
                "Provider is not active: " + providerId, Map.of("providerId", providerId));
    }

    public static AppException providerSecretMasterKeyMissing() {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_MASTER_KEY_MISSING);
    }

    public static AppException providerSecretMasterKeyInvalid(String reason) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_MASTER_KEY_INVALID,
                "Encryption master key is invalid: " + reason, Map.of("reason", reason));
    }

    public static AppException providerSecretEncryptionFailed(String reason) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_ENCRYPTION_FAILED,
                "Secret encryption failed: " + reason, Map.of("reason", reason));
    }

    public static AppException providerSecretDecryptionFailed(String reason) {
        return new AppException(AiAgentErrorCatalog.PROVIDER_SECRET_DECRYPTION_FAILED,
                "Secret decryption failed: " + reason, Map.of("reason", reason));
    }

    // ── Usage Policy Evaluator ────────────────────────────────────────────────

    public static AppException usagePolicyExceeded(String policyCode) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_EXCEEDED,
                "Usage policy limit exceeded" + (policyCode != null && !policyCode.isBlank()
                        ? ": " + policyCode : ""),
                Map.of());
    }

    public static AppException usagePolicyEvaluationFailed(String reason) {
        return new AppException(AiAgentErrorCatalog.USAGE_POLICY_EVALUATION_FAILED,
                "Usage policy evaluation failed: " + reason, Map.of("reason", reason));
    }

    // ── Playground ────────────────────────────────────────────────────────────

    public static AppException playgroundEventConfigNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_EVENT_CONFIG_NOT_FOUND,
                "Event configuration not found for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundEventConfigNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_EVENT_CONFIG_NOT_ACTIVE,
                "Event configuration is not active: " + id, Map.of("id", id));
    }

    public static AppException playgroundAgentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_AGENT_NOT_FOUND,
                "Agent not found for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundAgentNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_AGENT_NOT_ACTIVE,
                "Agent is not active: " + id, Map.of("id", id));
    }

    public static AppException playgroundPromptVersionNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_PROMPT_VERSION_NOT_FOUND,
                "Prompt version not found for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundPromptVersionNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_PROMPT_VERSION_NOT_ACTIVE,
                "Prompt version must be ACTIVE or DRAFT for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundPromptTemplateNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_PROMPT_TEMPLATE_NOT_FOUND,
                "Prompt template not found for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundPromptTemplateNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_PROMPT_TEMPLATE_NOT_ACTIVE,
                "Prompt template is not active: " + id, Map.of("id", id));
    }

    public static AppException playgroundPromptTemplateAgentMismatch(UUID templateId, UUID agentId) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_PROMPT_TEMPLATE_AGENT_MISMATCH,
                "Prompt template " + templateId + " does not belong to agent " + agentId,
                Map.of("templateId", templateId, "agentId", agentId));
    }

    public static AppException playgroundModelDeploymentNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_MODEL_DEPLOYMENT_NOT_FOUND,
                "Model deployment not found for playground: " + id, Map.of("id", id));
    }

    public static AppException playgroundModelDeploymentNotActive(UUID id) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_MODEL_DEPLOYMENT_NOT_ACTIVE,
                "Model deployment is not active: " + id, Map.of("id", id));
    }

    public static AppException playgroundDisabledInEnvironment(String environment) {
        return new AppException(AiAgentErrorCatalog.PLAYGROUND_DISABLED_IN_ENVIRONMENT,
                "Playground direct execution is disabled in environment: " + environment,
                Map.of("environment", environment));
    }

    // ── Execution Lifecycle ───────────────────────────────────────────────────

    public static AppException executionLifecycleWriteRestricted() {
        return new AppException(AiAgentErrorCatalog.EXECUTION_LIFECYCLE_WRITE_RESTRICTED);
    }

    public static AppException invalidExecutionInput(String reason) {
        return new AppException(AiAgentErrorCatalog.AI_INVALID_INPUT_VARIABLES,
                "AI execution input is invalid: " + reason, Map.of("reason", reason));
    }

    public static AppException invalidExecutionOutput(String reason) {
        return new AppException(AiAgentErrorCatalog.AI_INVALID_OUTPUT,
                "AI execution output is invalid: " + reason, Map.of("reason", reason));
    }

    // ── AI Tool registry (AIG-012) ────────────────────────────────────────────

    public static AppException aiToolNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_NOT_FOUND,
                "AI tool not found: " + id, Map.of("id", id));
    }

    public static AppException aiToolCodeAlreadyExists(String code) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_CODE_ALREADY_EXISTS,
                "AI tool code already exists: " + code, Map.of("code", code));
    }

    public static AppException aiToolNotActive(String code) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_NOT_ACTIVE,
                "AI tool is not active: " + code, Map.of("code", code));
    }

    public static AppException deprecatedAiToolCannotBeActivated(String code) {
        return new AppException(AiAgentErrorCatalog.DEPRECATED_AI_TOOL_CANNOT_BE_ACTIVATED,
                "Deprecated AI tool cannot be activated again: " + code, Map.of("code", code));
    }

    public static AppException aiToolPermissionNotFound(UUID id) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_PERMISSION_NOT_FOUND,
                "AI tool permission not found: " + id, Map.of("id", id));
    }

    public static AppException aiToolPermissionAlreadyExists(String toolCode, String permissionCode) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_PERMISSION_ALREADY_EXISTS,
                "Permission already exists on tool " + toolCode + ": " + permissionCode,
                Map.of("toolCode", toolCode, "permissionCode", permissionCode));
    }

    public static AppException aiToolBindingNotFound(UUID agentId, UUID toolId) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_BINDING_NOT_FOUND,
                "AI agent tool binding not found for agent " + agentId + " and tool " + toolId,
                Map.of("agentId", agentId, "toolId", toolId));
    }

    public static AppException aiToolBindingAlreadyExists(String agentCode, String toolCode) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_BINDING_ALREADY_EXISTS,
                "Binding already exists for agent " + agentCode + " and tool " + toolCode,
                Map.of("agentCode", agentCode, "toolCode", toolCode));
    }

    public static AppException aiToolBindingNotActive(UUID agentId, UUID toolId) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_BINDING_NOT_ACTIVE,
                "AI agent tool binding is not active for agent " + agentId + " and tool " + toolId,
                Map.of("agentId", agentId, "toolId", toolId));
    }

    public static AppException aiToolAgentNotActive(String agentCode) {
        return new AppException(AiAgentErrorCatalog.AI_TOOL_AGENT_NOT_ACTIVE,
                "Agent must be active to bind tools: " + agentCode, Map.of("agentCode", agentCode));
    }
}
