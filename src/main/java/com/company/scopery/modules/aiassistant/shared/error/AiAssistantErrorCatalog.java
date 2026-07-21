package com.company.scopery.modules.aiassistant.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum AiAssistantErrorCatalog implements ErrorCatalog {

    AI_CONVERSATION_NOT_FOUND(
            "AI_CONVERSATION_NOT_FOUND",
            "AI conversation not found",
            HttpStatus.NOT_FOUND),

    AI_CONVERSATION_ACCESS_DENIED(
            "AI_CONVERSATION_ACCESS_DENIED",
            "Access denied to this AI conversation",
            HttpStatus.FORBIDDEN),

    AI_CONVERSATION_PROJECT_SCOPE_MISMATCH(
            "AI_CONVERSATION_PROJECT_SCOPE_MISMATCH",
            "Conversation project scope does not match the requested context",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_CONVERSATION_MESSAGE_LIMIT_EXCEEDED(
            "AI_CONVERSATION_MESSAGE_LIMIT_EXCEEDED",
            "Conversation has reached the maximum number of messages",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_MESSAGE_NOT_FOUND(
            "AI_MESSAGE_NOT_FOUND",
            "AI message not found",
            HttpStatus.NOT_FOUND),

    AI_MESSAGE_TOO_LONG(
            "AI_MESSAGE_TOO_LONG",
            "Message exceeds the maximum allowed character length",
            HttpStatus.BAD_REQUEST),

    AI_MESSAGE_CONTEXT_TOO_LARGE(
            "AI_MESSAGE_CONTEXT_TOO_LARGE",
            "Message context exceeds the maximum token budget",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_MESSAGE_BLOCKED_BY_POLICY(
            "AI_MESSAGE_BLOCKED_BY_POLICY",
            "Message was blocked by content policy",
            HttpStatus.UNPROCESSABLE_ENTITY),

    AI_MESSAGE_IDEMPOTENCY_CONFLICT(
            "AI_MESSAGE_IDEMPOTENCY_CONFLICT",
            "A message with the same idempotency key already exists in this conversation",
            HttpStatus.CONFLICT),

    AI_QUOTA_DAILY_TURNS_EXCEEDED(
            "AI_QUOTA_DAILY_TURNS_EXCEEDED",
            "Daily turn quota exceeded",
            HttpStatus.TOO_MANY_REQUESTS),

    AI_QUOTA_DAILY_TOKENS_EXCEEDED(
            "AI_QUOTA_DAILY_TOKENS_EXCEEDED",
            "Daily token quota exceeded",
            HttpStatus.TOO_MANY_REQUESTS),

    AI_QUOTA_ACTIVE_STREAMS_EXCEEDED(
            "AI_QUOTA_ACTIVE_STREAMS_EXCEEDED",
            "Maximum number of concurrent active streams exceeded",
            HttpStatus.TOO_MANY_REQUESTS),

    AI_ASSISTANT_MODEL_UNAVAILABLE(
            "AI_ASSISTANT_MODEL_UNAVAILABLE",
            "AI assistant model is currently unavailable",
            HttpStatus.SERVICE_UNAVAILABLE),

    AI_STREAM_EVENT_LIMIT_EXCEEDED(
            "AI_STREAM_EVENT_LIMIT_EXCEEDED",
            "Stream event limit exceeded during generation",
            HttpStatus.INTERNAL_SERVER_ERROR),

    AI_CONTEXT_ENTITY_NOT_FOUND(
            "AI_CONTEXT_ENTITY_NOT_FOUND",
            "Context entity not found",
            HttpStatus.NOT_FOUND),

    AI_CONTEXT_ACTION_UNKNOWN(
            "AI_CONTEXT_ACTION_UNKNOWN",
            "Unknown context action",
            HttpStatus.BAD_REQUEST),

    AI_CONTEXT_ACCESS_DENIED(
            "AI_CONTEXT_ACCESS_DENIED",
            "Access denied to the requested context",
            HttpStatus.FORBIDDEN),

    AI_CONTEXT_PAGE_UNKNOWN(
            "AI_CONTEXT_PAGE_UNKNOWN",
            "Unknown page context",
            HttpStatus.BAD_REQUEST),

    AI_TOOL_CALL_FAILED(
            "AI_TOOL_CALL_FAILED",
            "AI tool call failed during execution",
            HttpStatus.INTERNAL_SERVER_ERROR),

    AI_TOOL_NOT_ALLOWED(
            "AI_TOOL_NOT_ALLOWED",
            "AI tool is not allowed in this context",
            HttpStatus.FORBIDDEN),

    AI_GUIDE_NOT_AVAILABLE(
            "AI_GUIDE_NOT_AVAILABLE",
            "AI contextual guide is not yet available. Please use the AI Assistant chat instead.",
            HttpStatus.SERVICE_UNAVAILABLE),

    AI_WORKSPACE_CONFIG_NOT_FOUND(
            "AI_WORKSPACE_CONFIG_NOT_FOUND",
            "AI Assistant workspace configuration not found",
            HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    AiAssistantErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
