package com.company.scopery.modules.traceability.aimapping.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiMappingErrorCatalog implements ErrorCatalog {

    MAPPING_RUN_NOT_FOUND(
            "MAPPING_RUN_NOT_FOUND",
            "Mapping run not found.",
            HttpStatus.NOT_FOUND),

    MAPPING_RUN_STILL_RUNNING(
            "MAPPING_RUN_STILL_RUNNING",
            "A mapping run is already in progress for this project and relation type.",
            HttpStatus.CONFLICT),

    MAPPING_SUGGESTION_NOT_FOUND(
            "MAPPING_SUGGESTION_NOT_FOUND",
            "Mapping suggestion not found.",
            HttpStatus.NOT_FOUND),

    MAPPING_SUGGESTION_INVALID_STATUS(
            "MAPPING_SUGGESTION_INVALID_STATUS",
            "Operation not allowed in the current suggestion review status.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MAPPING_STALE_SUGGESTION(
            "MAPPING_STALE_SUGGESTION",
            "Suggestion is stale — source or target has changed since generation.",
            HttpStatus.CONFLICT),

    MAPPING_CARDINALITY_VIOLATION(
            "MAPPING_CARDINALITY_VIOLATION",
            "Target already has a parent mapping; replace it explicitly via the remap endpoint.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MAPPING_ALREADY_EXISTS(
            "MAPPING_ALREADY_EXISTS",
            "A confirmed mapping already exists between these entities.",
            HttpStatus.CONFLICT),

    MAPPING_SOURCE_INELIGIBLE(
            "MAPPING_SOURCE_INELIGIBLE",
            "Source item is not eligible for AI mapping.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MAPPING_NO_DEPLOYMENT_CONFIGURED(
            "MAPPING_NO_DEPLOYMENT_CONFIGURED",
            "No AI model deployment is configured for mapping suggestions.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MAPPING_PROMPT_NOT_FOUND(
            "MAPPING_PROMPT_NOT_FOUND",
            "Active prompt version not found for this mapping action.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    MAPPING_AI_CALL_FAILED(
            "MAPPING_AI_CALL_FAILED",
            "AI provider call failed while generating mapping suggestions.",
            HttpStatus.INTERNAL_SERVER_ERROR),

    MAPPING_AI_RESPONSE_INVALID(
            "MAPPING_AI_RESPONSE_INVALID",
            "AI provider returned an unexpected response format.",
            HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiMappingErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
