package com.company.scopery.modules.iam.right.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search/filter parameters for listing IAM system rights")
public record SearchIamRightRequest(
        @Schema(description = "Full-text keyword to search in code, name, or description", example = "AI Platform", nullable = true)
        String keyword,

        @Schema(description = "Filter by the module code that owns the right (e.g. AIAGENT, IAM)", example = "AIAGENT", nullable = true)
        String module,

        @Schema(description = "Filter by right status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"}, nullable = true)
        String status,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "50")
        int size) {

    public SearchIamRightRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 50;
    }
}
