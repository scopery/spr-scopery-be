package com.company.scopery.modules.traceability.aimapping.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiMappingExceptions {

    private AiMappingExceptions() {}

    public static AppException runNotFound(UUID runId) {
        return new AppException(AiMappingErrorCatalog.MAPPING_RUN_NOT_FOUND,
                "Mapping run not found: " + runId,
                Map.of("runId", runId.toString()));
    }

    public static AppException runStillRunning(UUID projectId, String relationType) {
        return new AppException(AiMappingErrorCatalog.MAPPING_RUN_STILL_RUNNING,
                "A mapping run is already in progress for project " + projectId + " / " + relationType,
                Map.of("projectId", projectId.toString(), "relationType", relationType));
    }

    public static AppException suggestionNotFound(UUID suggestionId) {
        return new AppException(AiMappingErrorCatalog.MAPPING_SUGGESTION_NOT_FOUND,
                "Mapping suggestion not found: " + suggestionId,
                Map.of("suggestionId", suggestionId.toString()));
    }

    public static AppException suggestionInvalidStatus(UUID suggestionId, String currentStatus, String operation) {
        return new AppException(AiMappingErrorCatalog.MAPPING_SUGGESTION_INVALID_STATUS,
                "Operation '" + operation + "' not allowed when review status is " + currentStatus,
                Map.of("suggestionId", suggestionId.toString(), "currentStatus", currentStatus, "operation", operation));
    }

    public static AppException staleSuggestion(UUID suggestionId) {
        return new AppException(AiMappingErrorCatalog.MAPPING_STALE_SUGGESTION,
                "Suggestion is stale and cannot be applied: " + suggestionId,
                Map.of("suggestionId", suggestionId.toString()));
    }

    public static AppException cardinalityViolation(UUID sourceId, String sourceType) {
        return new AppException(AiMappingErrorCatalog.MAPPING_CARDINALITY_VIOLATION,
                sourceType + " " + sourceId + " already has a parent mapping.",
                Map.of("sourceId", sourceId.toString(), "sourceType", sourceType));
    }

    public static AppException mappingAlreadyExists(UUID sourceId, UUID targetId) {
        return new AppException(AiMappingErrorCatalog.MAPPING_ALREADY_EXISTS,
                "Mapping already exists between " + sourceId + " and " + targetId,
                Map.of("sourceId", sourceId.toString(), "targetId", targetId.toString()));
    }

    public static AppException noDeploymentConfigured() {
        return new AppException(AiMappingErrorCatalog.MAPPING_NO_DEPLOYMENT_CONFIGURED,
                "Configure scopery.ai-mapping.default-model-deployment-id to enable AI mapping suggestions.",
                Map.of());
    }

    public static AppException promptNotFound(String promptCode) {
        return new AppException(AiMappingErrorCatalog.MAPPING_PROMPT_NOT_FOUND,
                "No active prompt version found for code: " + promptCode,
                Map.of("promptCode", promptCode));
    }

    public static AppException aiCallFailed(String reason) {
        return new AppException(AiMappingErrorCatalog.MAPPING_AI_CALL_FAILED,
                "AI provider call failed: " + reason,
                Map.of("reason", reason));
    }

    public static AppException aiResponseInvalid(String detail) {
        return new AppException(AiMappingErrorCatalog.MAPPING_AI_RESPONSE_INVALID,
                "AI response could not be parsed: " + detail,
                Map.of("detail", detail));
    }
}
