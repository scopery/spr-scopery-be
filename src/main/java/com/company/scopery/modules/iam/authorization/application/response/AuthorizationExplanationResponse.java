package com.company.scopery.modules.iam.authorization.application.response;

import com.company.scopery.modules.iam.authorization.domain.model.AuthorizationExplanation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Detailed explanation of an authorization decision, including the contributing grants")
public record AuthorizationExplanationResponse(
        @Schema(description = "Permission code being evaluated", example = "AI_PLATFORM_MANAGE")
        String permissionCode,

        @Schema(description = "Action code being evaluated within the permission", example = "EXECUTE")
        String actionCode,

        @Schema(description = "Resource type the authorization applies to", example = "AI_AGENT")
        String resourceType,

        @Schema(description = "Reference ID of the specific resource instance", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID resourceRefId,

        @Schema(description = "Whether the subject is allowed to perform the action", example = "true")
        boolean allowed,

        @Schema(description = "Short reason code for the decision (e.g. GRANT_FOUND, NO_GRANT, DENIED)", example = "GRANT_FOUND")
        String reason,

        @Schema(description = "List of grant IDs that contributed to the allow/deny decision", nullable = true)
        List<UUID> contributingGrantIds,

        @Schema(description = "Human-readable explanation of how the decision was reached", example = "Access allowed via role ADMIN through grant abc-123")
        String explanation) {

    public static AuthorizationExplanationResponse from(
            String permissionCode, String actionCode, String resourceType, UUID resourceRefId,
            AuthorizationExplanation explanation) {
        return new AuthorizationExplanationResponse(permissionCode, actionCode, resourceType, resourceRefId,
                explanation.allowed(), explanation.reason(), explanation.contributingGrantIds(),
                explanation.explanation());
    }
}
