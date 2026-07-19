package com.company.scopery.modules.airecommendation.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiRecommendationErrorCatalog implements ErrorCatalog {

    // ── Policy ────────────────────────────────────────────────────────────────

    AI_RECOMMENDATION_POLICY_NOT_FOUND(
            "AI_RECOMMENDATION_POLICY_NOT_FOUND",
            "Recommendation policy not found.",
            HttpStatus.NOT_FOUND),

    AI_RECOMMENDATION_POLICY_INACTIVE(
            "AI_RECOMMENDATION_POLICY_INACTIVE",
            "Recommendation policy is not active.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Pack ──────────────────────────────────────────────────────────────────

    AI_RECOMMENDATION_PACK_NOT_FOUND(
            "AI_RECOMMENDATION_PACK_NOT_FOUND",
            "Recommendation pack not found.",
            HttpStatus.NOT_FOUND),

    AI_RECOMMENDATION_PACK_INACTIVE(
            "AI_RECOMMENDATION_PACK_INACTIVE",
            "Recommendation pack is not active.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Run ───────────────────────────────────────────────────────────────────

    AI_RECOMMENDATION_RUN_NOT_FOUND(
            "AI_RECOMMENDATION_RUN_NOT_FOUND",
            "Recommendation run not found.",
            HttpStatus.NOT_FOUND),

    AI_RECOMMENDATION_RUN_FAILED(
            "AI_RECOMMENDATION_RUN_FAILED",
            "Recommendation run failed.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_RECOMMENDATION_CONTEXT_ACCESS_DENIED(
            "AI_RECOMMENDATION_CONTEXT_ACCESS_DENIED",
            "Access denied to the recommendation context.",
            HttpStatus.FORBIDDEN),

    // ── Suggestion reference ──────────────────────────────────────────────────

    AI_SUGGESTION_REFERENCE_INVALID(
            "AI_SUGGESTION_REFERENCE_INVALID",
            "Invalid suggestion reference format.",
            HttpStatus.BAD_REQUEST),

    AI_SUGGESTION_NOT_FOUND(
            "AI_SUGGESTION_NOT_FOUND",
            "Suggestion not found.",
            HttpStatus.NOT_FOUND),

    AI_SUGGESTION_ACCESS_DENIED(
            "AI_SUGGESTION_ACCESS_DENIED",
            "Access denied to this suggestion.",
            HttpStatus.FORBIDDEN),

    // ── Suggestion lifecycle ──────────────────────────────────────────────────

    AI_SUGGESTION_INVALID_STATUS(
            "AI_SUGGESTION_INVALID_STATUS",
            "Operation not allowed in the current suggestion status.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_SUGGESTION_VERSION_CONFLICT(
            "AI_SUGGESTION_VERSION_CONFLICT",
            "Suggestion version conflict; another update has been applied.",
            HttpStatus.CONFLICT),

    AI_SUGGESTION_STALE(
            "AI_SUGGESTION_STALE",
            "The suggestion is stale and must be regenerated.",
            HttpStatus.CONFLICT),

    AI_SUGGESTION_SCHEMA_INVALID(
            "AI_SUGGESTION_SCHEMA_INVALID",
            "Proposed payload does not conform to the suggestion schema.",
            HttpStatus.BAD_REQUEST),

    AI_SUGGESTION_TARGET_NOT_FOUND(
            "AI_SUGGESTION_TARGET_NOT_FOUND",
            "Target entity for this suggestion was not found.",
            HttpStatus.NOT_FOUND),

    AI_SUGGESTION_EVIDENCE_MISSING(
            "AI_SUGGESTION_EVIDENCE_MISSING",
            "No valid evidence is attached to this suggestion.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_SUGGESTION_EVIDENCE_ACCESS_DENIED(
            "AI_SUGGESTION_EVIDENCE_ACCESS_DENIED",
            "Access denied to suggestion evidence.",
            HttpStatus.FORBIDDEN),

    AI_SUGGESTION_DUPLICATE(
            "AI_SUGGESTION_DUPLICATE",
            "An equivalent active suggestion already exists.",
            HttpStatus.CONFLICT),

    AI_SUGGESTION_SUPPRESSION_FORBIDDEN(
            "AI_SUGGESTION_SUPPRESSION_FORBIDDEN",
            "This suggestion cannot be suppressed.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE(
            "AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE",
            "Action-plan preparation is not available until Phase 44 is enabled.",
            HttpStatus.CONFLICT),

    AI_SUGGESTION_IMPACT_UNVERIFIED(
            "AI_SUGGESTION_IMPACT_UNVERIFIED",
            "Numeric impact requires a verified calculation source.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    // ── Legacy Phase 21 ───────────────────────────────────────────────────────

    AI_LEGACY_SUGGESTION_EDIT_UNAVAILABLE(
            "AI_LEGACY_SUGGESTION_EDIT_UNAVAILABLE",
            "Edit is not supported for legacy Phase 21 suggestions.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_LEGACY_SUGGESTION_SUPPRESSION_UNAVAILABLE(
            "AI_LEGACY_SUGGESTION_SUPPRESSION_UNAVAILABLE",
            "Suppression is not supported for legacy Phase 21 suggestions.",
            HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiRecommendationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() { return code; }

    @Override
    public String defaultMessage() { return defaultMessage; }

    @Override
    public HttpStatus httpStatus() { return httpStatus; }
}
