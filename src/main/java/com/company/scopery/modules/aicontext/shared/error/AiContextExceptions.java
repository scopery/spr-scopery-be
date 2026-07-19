package com.company.scopery.modules.aicontext.shared.error;

import com.company.scopery.common.exception.AppException;
import java.util.Map;
import java.util.UUID;

public final class AiContextExceptions {
    private AiContextExceptions() {}

    public static AppException policyNotFound(String policyCode) {
        return new AppException(AiContextErrorCatalog.RESOLUTION_POLICY_NOT_FOUND, "Policy not found: " + policyCode, Map.of("policyCode", policyCode));
    }

    public static AppException auditNotFound(UUID id) {
        return new AppException(AiContextErrorCatalog.RESOLUTION_AUDIT_NOT_FOUND, "Audit record not found: " + id, Map.of("id", id));
    }

    public static AppException resourceReadDenied(String resourceRef) {
        return new AppException(AiContextErrorCatalog.RESOURCE_READ_DENIED, "AI_READ denied for: " + resourceRef, Map.of("resourceRef", resourceRef));
    }

    public static AppException aiProcessingPolicyBlocked(String resourceRef) {
        return new AppException(AiContextErrorCatalog.AI_PROCESSING_POLICY_BLOCKED, "AI processing blocked for: " + resourceRef, Map.of("resourceRef", resourceRef));
    }

    public static AppException contextTooLarge(long actualChars, long maxChars) {
        return new AppException(AiContextErrorCatalog.CONTEXT_TOO_LARGE, "Context size " + actualChars + " exceeds max " + maxChars, Map.of("actualChars", actualChars, "maxChars", maxChars));
    }
}
