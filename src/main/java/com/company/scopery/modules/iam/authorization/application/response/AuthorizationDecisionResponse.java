package com.company.scopery.modules.iam.authorization.application.response;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationDecision;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Result of an authorization check for a specific user, resource, and right")
public record AuthorizationDecisionResponse(
        @Schema(description = "ID of the user whose access was evaluated", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID userId,

        @Schema(description = "ID of the resource the authorization was checked against", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID resourceId,

        @Schema(description = "System right code that was evaluated", example = "AI_PLATFORM_MANAGE")
        String rightCode,

        @Schema(description = "Whether the user is allowed to perform the action", example = "true")
        boolean allowed,

        @Schema(description = "Human-readable reason for the authorization decision (e.g. GRANT_FOUND, NO_GRANT)", example = "GRANT_FOUND")
        String reason) {

    public static AuthorizationDecisionResponse from(UUID userId, UUID resourceId,
                                                      String rightCode, AuthorizationDecision decision) {
        return new AuthorizationDecisionResponse(
                userId, resourceId, rightCode, decision.allowed(), decision.reason().name());
    }
}
