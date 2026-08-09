package com.company.scopery.modules.elicitation.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class ElicitationExceptions {

    private ElicitationExceptions() {}

    // ── Session ───────────────────────────────────────────────────────────────

    public static AppException sessionNotFound(UUID id) {
        return new AppException(ElicitationErrorCatalog.SESSION_NOT_FOUND,
                "Elicitation session not found: " + id, Map.of("id", id));
    }

    public static AppException sessionScopePackageNotFound(UUID scopePackageId) {
        return new AppException(ElicitationErrorCatalog.SESSION_SCOPE_PACKAGE_NOT_FOUND,
                "Scope package not found: " + scopePackageId, Map.of("scopePackageId", scopePackageId));
    }

    public static AppException sessionAlreadyActive(UUID scopePackageId) {
        return new AppException(ElicitationErrorCatalog.SESSION_ALREADY_ACTIVE,
                "An active elicitation session already exists for scope package: " + scopePackageId,
                Map.of("scopePackageId", scopePackageId));
    }

    public static AppException sessionNotActive(UUID sessionId) {
        return new AppException(ElicitationErrorCatalog.SESSION_NOT_ACTIVE,
                "Elicitation session is not active: " + sessionId, Map.of("sessionId", sessionId));
    }

    public static AppException sessionAlreadyClosed(UUID sessionId) {
        return new AppException(ElicitationErrorCatalog.SESSION_ALREADY_CLOSED,
                "Elicitation session is already closed: " + sessionId, Map.of("sessionId", sessionId));
    }

    public static AppException sessionAlreadyCancelled(UUID sessionId) {
        return new AppException(ElicitationErrorCatalog.SESSION_ALREADY_CANCELLED,
                "Elicitation session is already cancelled: " + sessionId, Map.of("sessionId", sessionId));
    }

    public static AppException sessionNoAnsweredQuestions(UUID sessionId) {
        return new AppException(ElicitationErrorCatalog.SESSION_NO_ANSWERED_QUESTIONS,
                "Cannot close session with no answered questions: " + sessionId, Map.of("sessionId", sessionId));
    }

    // ── Question ──────────────────────────────────────────────────────────────

    public static AppException questionNotFound(UUID id) {
        return new AppException(ElicitationErrorCatalog.QUESTION_NOT_FOUND,
                "Elicitation question not found: " + id, Map.of("id", id));
    }

    public static AppException questionAlreadyAnswered(UUID questionId) {
        return new AppException(ElicitationErrorCatalog.QUESTION_ALREADY_ANSWERED,
                "Question is already answered: " + questionId, Map.of("questionId", questionId));
    }

    public static AppException questionAlreadySkipped(UUID questionId) {
        return new AppException(ElicitationErrorCatalog.QUESTION_ALREADY_SKIPPED,
                "Question is already skipped: " + questionId, Map.of("questionId", questionId));
    }

    public static AppException questionNotPending(UUID questionId) {
        return new AppException(ElicitationErrorCatalog.QUESTION_NOT_PENDING,
                "Question must be in PENDING status: " + questionId, Map.of("questionId", questionId));
    }

    // ── Round ─────────────────────────────────────────────────────────────────

    public static AppException roundNotFound(UUID id) {
        return new AppException(ElicitationErrorCatalog.ROUND_NOT_FOUND,
                "Elicitation round not found: " + id, Map.of("id", id));
    }

    public static AppException roundAlreadySubmitted(UUID roundId) {
        return new AppException(ElicitationErrorCatalog.ROUND_ALREADY_SUBMITTED,
                "Elicitation round is already submitted: " + roundId, Map.of("roundId", roundId));
    }

    public static AppException roundNotSubmitted(UUID roundId) {
        return new AppException(ElicitationErrorCatalog.ROUND_NOT_SUBMITTED,
                "Elicitation round must be submitted first: " + roundId, Map.of("roundId", roundId));
    }

    // ── Suggestion ────────────────────────────────────────────────────────────

    public static AppException suggestionNotFound(UUID id) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_NOT_FOUND,
                "Elicitation suggestion not found: " + id, Map.of("id", id));
    }

    public static AppException suggestionItemNotFound(UUID id) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_ITEM_NOT_FOUND,
                "Elicitation suggestion item not found: " + id, Map.of("id", id));
    }

    public static AppException suggestionItemNotPending(UUID itemId) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_ITEM_NOT_PENDING,
                "Suggestion item must be in PENDING status: " + itemId, Map.of("itemId", itemId));
    }

    public static AppException suggestionItemAlreadyExecuted(UUID itemId) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_ITEM_ALREADY_EXECUTED,
                "Suggestion item has already been executed: " + itemId, Map.of("itemId", itemId));
    }

    public static AppException suggestionItemExecutionFailed(UUID itemId, String reason) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_ITEM_EXECUTION_FAILED,
                "Suggestion item execution failed: " + reason, Map.of("itemId", itemId));
    }

    public static AppException suggestionItemUnsupportedAction(UUID itemId, String action) {
        return new AppException(ElicitationErrorCatalog.SUGGESTION_ITEM_UNSUPPORTED_ACTION,
                "Unsupported action '" + action + "' for suggestion item: " + itemId,
                Map.of("itemId", itemId, "action", action));
    }

    // ── AI ────────────────────────────────────────────────────────────────────

    public static AppException elicitationAiCallFailed(UUID sessionId, String reason) {
        return new AppException(ElicitationErrorCatalog.ELICITATION_AI_CALL_FAILED,
                "AI call failed for session " + sessionId + ": " + reason,
                Map.of("sessionId", sessionId));
    }

    public static AppException elicitationAiResponseInvalid(UUID sessionId, String reason) {
        return new AppException(ElicitationErrorCatalog.ELICITATION_AI_RESPONSE_INVALID,
                "Invalid AI response for session " + sessionId + ": " + reason,
                Map.of("sessionId", sessionId));
    }
}
