package com.company.scopery.modules.elicitation.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum ElicitationErrorCatalog implements ErrorCatalog {

    // ── Session ───────────────────────────────────────────────────────────────

    SESSION_NOT_FOUND(
            "ELICITATION_SESSION_NOT_FOUND",
            "Elicitation session not found",
            HttpStatus.NOT_FOUND),

    SESSION_SCOPE_PACKAGE_NOT_FOUND(
            "ELICITATION_SESSION_SCOPE_PACKAGE_NOT_FOUND",
            "Scope package not found for this elicitation session",
            HttpStatus.NOT_FOUND),

    SESSION_ALREADY_ACTIVE(
            "ELICITATION_SESSION_ALREADY_ACTIVE",
            "An active elicitation session already exists for this scope package",
            HttpStatus.CONFLICT),

    SESSION_NOT_ACTIVE(
            "ELICITATION_SESSION_NOT_ACTIVE",
            "This operation requires an active elicitation session",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SESSION_ALREADY_CLOSED(
            "ELICITATION_SESSION_ALREADY_CLOSED",
            "Elicitation session is already closed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SESSION_ALREADY_CANCELLED(
            "ELICITATION_SESSION_ALREADY_CANCELLED",
            "Elicitation session is already cancelled",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SESSION_NO_ANSWERED_QUESTIONS(
            "ELICITATION_SESSION_NO_ANSWERED_QUESTIONS",
            "Cannot close a session with no answered questions",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_SESSION_STATUS(
            "ELICITATION_INVALID_SESSION_STATUS",
            "Invalid elicitation session status",
            HttpStatus.BAD_REQUEST),

    // ── Question ──────────────────────────────────────────────────────────────

    QUESTION_NOT_FOUND(
            "ELICITATION_QUESTION_NOT_FOUND",
            "Elicitation question not found",
            HttpStatus.NOT_FOUND),

    QUESTION_ALREADY_ANSWERED(
            "ELICITATION_QUESTION_ALREADY_ANSWERED",
            "Elicitation question is already answered",
            HttpStatus.UNPROCESSABLE_ENTITY),

    QUESTION_ALREADY_SKIPPED(
            "ELICITATION_QUESTION_ALREADY_SKIPPED",
            "Elicitation question is already skipped",
            HttpStatus.UNPROCESSABLE_ENTITY),

    QUESTION_NOT_PENDING(
            "ELICITATION_QUESTION_NOT_PENDING",
            "Elicitation question must be in PENDING status for this operation",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_QUESTION_STATUS(
            "ELICITATION_INVALID_QUESTION_STATUS",
            "Invalid elicitation question status",
            HttpStatus.BAD_REQUEST),

    INVALID_QUESTION_SOURCE(
            "ELICITATION_INVALID_QUESTION_SOURCE",
            "Invalid elicitation question source",
            HttpStatus.BAD_REQUEST),

    INVALID_CLARITY_LEVEL(
            "ELICITATION_INVALID_CLARITY_LEVEL",
            "Invalid clarity level",
            HttpStatus.BAD_REQUEST),

    // ── Round ─────────────────────────────────────────────────────────────────

    ROUND_NOT_FOUND(
            "ELICITATION_ROUND_NOT_FOUND",
            "Elicitation round not found",
            HttpStatus.NOT_FOUND),

    ROUND_ALREADY_ACTIVE(
            "ELICITATION_ROUND_ALREADY_ACTIVE",
            "An active round already exists for this session",
            HttpStatus.CONFLICT),

    ROUND_NOT_ACTIVE(
            "ELICITATION_ROUND_NOT_ACTIVE",
            "This operation requires an active round",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ROUND_NO_ANSWERED_QUESTIONS(
            "ELICITATION_ROUND_NO_ANSWERED_QUESTIONS",
            "Cannot submit a round with no answered questions",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_ROUND_STATUS(
            "ELICITATION_INVALID_ROUND_STATUS",
            "Invalid elicitation round status",
            HttpStatus.BAD_REQUEST),

    // ── Suggestion ────────────────────────────────────────────────────────────

    SUGGESTION_NOT_FOUND(
            "ELICITATION_SUGGESTION_NOT_FOUND",
            "Elicitation suggestion not found",
            HttpStatus.NOT_FOUND),

    SUGGESTION_ITEM_NOT_FOUND(
            "ELICITATION_SUGGESTION_ITEM_NOT_FOUND",
            "Elicitation suggestion item not found",
            HttpStatus.NOT_FOUND),

    SUGGESTION_ITEM_NOT_PENDING(
            "ELICITATION_SUGGESTION_ITEM_NOT_PENDING",
            "Suggestion item must be in PENDING status to approve or reject",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SUGGESTION_ITEM_ALREADY_EXECUTED(
            "ELICITATION_SUGGESTION_ITEM_ALREADY_EXECUTED",
            "Suggestion item has already been executed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SUGGESTION_ITEM_EXECUTION_FAILED(
            "ELICITATION_SUGGESTION_ITEM_EXECUTION_FAILED",
            "Suggestion item execution failed",
            HttpStatus.UNPROCESSABLE_ENTITY),

    SUGGESTION_ITEM_UNSUPPORTED_ACTION(
            "ELICITATION_SUGGESTION_ITEM_UNSUPPORTED_ACTION",
            "Suggestion item action is not supported",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_SUGGESTION_STATUS(
            "ELICITATION_INVALID_SUGGESTION_STATUS",
            "Invalid elicitation suggestion status",
            HttpStatus.BAD_REQUEST),

    INVALID_SUGGESTION_ITEM_STATUS(
            "ELICITATION_INVALID_SUGGESTION_ITEM_STATUS",
            "Invalid elicitation suggestion item status",
            HttpStatus.BAD_REQUEST),

    // ── AI ────────────────────────────────────────────────────────────────────

    ELICITATION_AI_CALL_FAILED(
            "ELICITATION_AI_CALL_FAILED",
            "AI call for elicitation failed",
            HttpStatus.BAD_GATEWAY),

    ELICITATION_AI_RESPONSE_INVALID(
            "ELICITATION_AI_RESPONSE_INVALID",
            "AI returned an invalid or unparseable response for elicitation",
            HttpStatus.UNPROCESSABLE_ENTITY);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ElicitationErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
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
