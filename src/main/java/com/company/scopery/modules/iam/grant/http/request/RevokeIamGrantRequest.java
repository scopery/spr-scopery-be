package com.company.scopery.modules.iam.grant.http.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request to revoke an active IAM access grant")
public record RevokeIamGrantRequest(
        @Schema(description = "ID of the grant to revoke", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull UUID grantId,

        @Schema(description = "Human-readable reason for revoking the grant", example = "Project ended; access no longer required", nullable = true)
        String reason) {
}
