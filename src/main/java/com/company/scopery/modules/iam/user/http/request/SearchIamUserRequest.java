package com.company.scopery.modules.iam.user.http.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search/filter parameters for listing IAM users")
public record SearchIamUserRequest(
        @Schema(description = "Full-text keyword to search in username, email, or full name", example = "john", nullable = true)
        String keyword,

        @Schema(description = "Filter by user account status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "SUSPENDED"}, nullable = true)
        String status,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size (number of items per page)", example = "20")
        int size) {

    public SearchIamUserRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
    }
}
