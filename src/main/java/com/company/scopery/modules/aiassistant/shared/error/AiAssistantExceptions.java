package com.company.scopery.modules.aiassistant.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class AiAssistantExceptions {

    private AiAssistantExceptions() {}

    public static AppException conversationNotFound(UUID id) {
        return new AppException(AiAssistantErrorCatalog.AI_CONVERSATION_NOT_FOUND,
                "AI conversation not found: " + id, Map.of("id", id));
    }

    public static AppException conversationAccessDenied(UUID id) {
        return new AppException(AiAssistantErrorCatalog.AI_CONVERSATION_ACCESS_DENIED,
                "Access denied to conversation: " + id, Map.of("id", id));
    }

    public static AppException conversationProjectScopeMismatch(UUID conversationId, UUID projectId) {
        return new AppException(AiAssistantErrorCatalog.AI_CONVERSATION_PROJECT_SCOPE_MISMATCH,
                "Conversation project scope mismatch",
                Map.of("conversationId", conversationId, "projectId", projectId));
    }

    public static AppException conversationMessageLimitExceeded(UUID conversationId, int limit) {
        return new AppException(AiAssistantErrorCatalog.AI_CONVERSATION_MESSAGE_LIMIT_EXCEEDED,
                "Conversation has reached the maximum of " + limit + " messages",
                Map.of("conversationId", conversationId, "limit", limit));
    }

    public static AppException messageNotFound(UUID id) {
        return new AppException(AiAssistantErrorCatalog.AI_MESSAGE_NOT_FOUND,
                "AI message not found: " + id, Map.of("id", id));
    }

    public static AppException messageTooLong(int actualChars, int maxChars) {
        return new AppException(AiAssistantErrorCatalog.AI_MESSAGE_TOO_LONG,
                "Message length " + actualChars + " exceeds limit of " + maxChars,
                Map.of("actualChars", actualChars, "maxChars", maxChars));
    }

    public static AppException messageContextTooLarge(int estimatedTokens, int maxTokens) {
        return new AppException(AiAssistantErrorCatalog.AI_MESSAGE_CONTEXT_TOO_LARGE,
                "Estimated context " + estimatedTokens + " tokens exceeds limit of " + maxTokens,
                Map.of("estimatedTokens", estimatedTokens, "maxTokens", maxTokens));
    }

    public static AppException messageBlockedByPolicy(String reason) {
        return new AppException(AiAssistantErrorCatalog.AI_MESSAGE_BLOCKED_BY_POLICY,
                "Message blocked by policy: " + reason, Map.of());
    }

    public static AppException messageIdempotencyConflict(UUID conversationId, String idempotencyKey) {
        return new AppException(AiAssistantErrorCatalog.AI_MESSAGE_IDEMPOTENCY_CONFLICT,
                "Message with idempotency key already exists",
                Map.of("conversationId", conversationId, "idempotencyKey", idempotencyKey));
    }

    public static AppException quotaDailyTurnsExceeded(UUID actorId, int limit) {
        return new AppException(AiAssistantErrorCatalog.AI_QUOTA_DAILY_TURNS_EXCEEDED,
                "Daily turn quota of " + limit + " exceeded",
                Map.of("actorId", actorId, "limit", limit));
    }

    public static AppException quotaDailyTokensExceeded(UUID actorId, int limit) {
        return new AppException(AiAssistantErrorCatalog.AI_QUOTA_DAILY_TOKENS_EXCEEDED,
                "Daily token quota of " + limit + " exceeded",
                Map.of("actorId", actorId, "limit", limit));
    }

    public static AppException quotaActiveStreamsExceeded(UUID actorId, int limit) {
        return new AppException(AiAssistantErrorCatalog.AI_QUOTA_ACTIVE_STREAMS_EXCEEDED,
                "Maximum of " + limit + " concurrent active streams exceeded",
                Map.of("actorId", actorId, "limit", limit));
    }

    public static AppException modelUnavailable(String reason) {
        return new AppException(AiAssistantErrorCatalog.AI_ASSISTANT_MODEL_UNAVAILABLE,
                "AI assistant model unavailable: " + reason, Map.of());
    }

    public static AppException streamEventLimitExceeded(UUID messageId, int limit) {
        return new AppException(AiAssistantErrorCatalog.AI_STREAM_EVENT_LIMIT_EXCEEDED,
                "Stream event limit of " + limit + " exceeded for message: " + messageId,
                Map.of("messageId", messageId, "limit", limit));
    }

    public static AppException contextEntityNotFound(String entityType, UUID entityId) {
        return new AppException(AiAssistantErrorCatalog.AI_CONTEXT_ENTITY_NOT_FOUND,
                "Context entity not found: " + entityType + "/" + entityId,
                Map.of("entityType", entityType, "entityId", entityId));
    }

    public static AppException contextActionUnknown(String action) {
        return new AppException(AiAssistantErrorCatalog.AI_CONTEXT_ACTION_UNKNOWN,
                "Unknown context action: " + action, Map.of("action", action));
    }

    public static AppException contextAccessDenied(String entityType, UUID entityId) {
        return new AppException(AiAssistantErrorCatalog.AI_CONTEXT_ACCESS_DENIED,
                "Access denied to context: " + entityType + "/" + entityId,
                Map.of("entityType", entityType, "entityId", entityId));
    }

    public static AppException contextPageUnknown(String page) {
        return new AppException(AiAssistantErrorCatalog.AI_CONTEXT_PAGE_UNKNOWN,
                "Unknown page context: " + page, Map.of("page", page));
    }

    public static AppException toolCallFailed(String toolCode, String reason) {
        return new AppException(AiAssistantErrorCatalog.AI_TOOL_CALL_FAILED,
                "Tool call failed [" + toolCode + "]: " + reason,
                Map.of("toolCode", toolCode));
    }

    public static AppException toolNotAllowed(String toolCode) {
        return new AppException(AiAssistantErrorCatalog.AI_TOOL_NOT_ALLOWED,
                "Tool not allowed in this context: " + toolCode, Map.of("toolCode", toolCode));
    }
}
