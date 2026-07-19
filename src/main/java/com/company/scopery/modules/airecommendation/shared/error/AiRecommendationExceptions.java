package com.company.scopery.modules.airecommendation.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiRecommendationExceptions {

    private AiRecommendationExceptions() {}

    public static AppException policyNotFound(String code) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_POLICY_NOT_FOUND,
                "Recommendation policy not found: " + code,
                Map.of("policyCode", code));
    }

    public static AppException policyInactive(String code) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_POLICY_INACTIVE,
                "Recommendation policy is not active: " + code,
                Map.of("policyCode", code));
    }

    public static AppException packNotFound(String code) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_PACK_NOT_FOUND,
                "Recommendation pack not found: " + code,
                Map.of("packCode", code));
    }

    public static AppException packInactive(String code) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_PACK_INACTIVE,
                "Recommendation pack is not active: " + code,
                Map.of("packCode", code));
    }

    public static AppException runNotFound(UUID runId) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_RUN_NOT_FOUND,
                "Recommendation run not found: " + runId,
                Map.of("runId", runId.toString()));
    }

    public static AppException runFailed(UUID runId, String errorCode) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_RUN_FAILED,
                "Recommendation run failed: " + runId,
                Map.of("runId", runId.toString(), "errorCode", errorCode));
    }

    public static AppException contextAccessDenied(UUID projectId) {
        return new AppException(AiRecommendationErrorCatalog.AI_RECOMMENDATION_CONTEXT_ACCESS_DENIED,
                "Access denied to recommendation context for project: " + projectId,
                Map.of("projectId", projectId.toString()));
    }

    public static AppException suggestionReferenceInvalid(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_REFERENCE_INVALID,
                "Invalid suggestion reference: " + ref,
                Map.of("ref", ref));
    }

    public static AppException suggestionNotFound(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_NOT_FOUND,
                "Suggestion not found: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suggestionAccessDenied(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_ACCESS_DENIED,
                "Access denied to suggestion: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suggestionInvalidStatus(String ref, String currentStatus, String operation) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_INVALID_STATUS,
                "Operation '" + operation + "' not allowed when suggestion status is " + currentStatus,
                Map.of("suggestionRef", ref, "currentStatus", currentStatus, "operation", operation));
    }

    public static AppException suggestionVersionConflict(String ref, long expected, long actual) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_VERSION_CONFLICT,
                "Suggestion version conflict for: " + ref,
                Map.of("suggestionRef", ref, "expectedVersion", String.valueOf(expected), "actualVersion", String.valueOf(actual)));
    }

    public static AppException suggestionStale(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_STALE,
                "The suggestion is stale and must be regenerated: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suggestionSchemaInvalid(String schemaCode, int schemaVersion, String detail) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_SCHEMA_INVALID,
                "Payload does not conform to schema " + schemaCode + " v" + schemaVersion + ": " + detail,
                Map.of("schemaCode", schemaCode, "schemaVersion", String.valueOf(schemaVersion)));
    }

    public static AppException suggestionTargetNotFound(String entityType, UUID entityId) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_TARGET_NOT_FOUND,
                "Target entity not found: " + entityType + "/" + entityId,
                Map.of("entityType", entityType, "entityId", entityId.toString()));
    }

    public static AppException suggestionEvidenceMissing(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_EVIDENCE_MISSING,
                "No valid evidence for suggestion: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suggestionEvidenceAccessDenied(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_EVIDENCE_ACCESS_DENIED,
                "Evidence access denied for suggestion: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suggestionDuplicate(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_DUPLICATE,
                "An equivalent active suggestion already exists: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException suppressionForbidden(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_SUPPRESSION_FORBIDDEN,
                "Suggestion cannot be suppressed: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException prepareApplyUnavailable(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE,
                "Action-plan preparation is not available until Phase 44 is enabled.",
                Map.of("suggestionRef", ref,
                        "requiredCapability", "PHASE_44_AGENTIC_ACTIONS",
                        "reservedToolCode", "agent.action.prepare"));
    }

    public static AppException legacySuggestionEditUnavailable(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_LEGACY_SUGGESTION_EDIT_UNAVAILABLE,
                "Edit is not supported for legacy Phase 21 suggestion: " + ref,
                Map.of("suggestionRef", ref));
    }

    public static AppException legacySuggestionSuppressionUnavailable(String ref) {
        return new AppException(AiRecommendationErrorCatalog.AI_LEGACY_SUGGESTION_SUPPRESSION_UNAVAILABLE,
                "Suppression is not supported for legacy Phase 21 suggestion: " + ref,
                Map.of("suggestionRef", ref));
    }
}
