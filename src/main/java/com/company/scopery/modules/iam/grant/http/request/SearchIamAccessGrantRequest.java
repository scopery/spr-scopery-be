package com.company.scopery.modules.iam.grant.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Search/filter parameters for listing IAM access grants")
public record SearchIamAccessGrantRequest(
        @Schema(description = "Filter by subject ID (user or team)", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID subjectId,

        @Schema(description = "Filter by resource ID the grant applies to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID resourceId,

        @Schema(description = "Filter by workspace ID the grant is scoped to", example = "550e8400-e29b-41d4-a716-446655440000", nullable = true)
        UUID workspaceId,

        @Schema(description = "Filter by grant status", example = "ACTIVE", allowableValues = {"ACTIVE", "REVOKED", "EXPIRED"}, nullable = true)
        String status,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchIamAccessGrantRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
